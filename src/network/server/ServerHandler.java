// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.server;

import entity.Entity;
import entity.EntityPlayer;
import entity.EntityRegistry;
import network.Connection;
import network.packet.*;
import util.math.Quat4;
import util.math.Vec3;
import world.Level;
import world.WorldServer;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

// Main handler for the server side networking
public class ServerHandler {

	public Random random = new Random();

	// The world
	public WorldServer world;

	// The ServerSocket for the server
	private ServerSocket serverSocket;

	private final int TARGET_TPS = 30;
	private int CURRENT_TPS = 0;

	private boolean sendImmediately = false;

	public String defaultLevel = "level1_Welcome";

	// Two way maps for connections and id
	private HashMap<Long, Connection> connections = new HashMap<>();
	private HashMap<Connection, Long> connectionsLookup = new HashMap<>();

	// Incoming and outgoing packet queues
	public ArrayBlockingQueue<IncomingPacket> receiveQueue = new ArrayBlockingQueue<>(4096);
	public ArrayBlockingQueue<OutgoingPacket> outgoingQueue = new ArrayBlockingQueue<>(4096);

	// Queue for console commands
	public ArrayBlockingQueue<String> commandQueue = new ArrayBlockingQueue<>(4096);

	private ByteBuffer buffer = ByteBuffer.allocate(65536);

	public ServerHandler(int port) throws IOException {

		// Start server
		serverSocket = new ServerSocket(port);
		ServerListener listener = new ServerListener(this, serverSocket);

		// Create new world
		world = new WorldServer(this);

		// List all the files in the obj dir
		File[] fs = Level.baseDir.listFiles();

		if(fs == null) {
			throw new RuntimeException("Cannot find obj directory");
		}

		// Go through each time
		for(File f : fs) {
			if(f.isDirectory()) {
				String levelName = f.getName();
				try {
					// Attempt to load each level file
					addLevel(levelName);
					System.out.println("Loaded " + levelName);
				} catch (IOException e) {
					System.err.println("Cannot load level file: " + levelName);
					e.printStackTrace();
				}
			}
		}
	}

	// Add new level to the world
	public void addLevel(String levelName) throws IOException {
		Level level = new Level(levelName, world);
		level.loadLevelFromFile();
		world.levels.put(levelName, level);
	}

	// Queue up a packet to be broadcasted to everyone
	public void queueBroadcast(String level, Packet packet) {
		for (Map.Entry<Long, Connection> conn : connections.entrySet()) {
			if (world.entities.get(conn.getKey()).level.equals(level)) {
				// Send immediately if sendImmediately is true
				if (sendImmediately) {
					sendPacket(packet, conn.getKey());
				} else {
					// Queue it up otherwise
					try {
						outgoingQueue.put(new OutgoingPacket(conn.getKey(), packet));
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	// Queue a packet to be sent to a player
	public void queuePacket(long id, Packet packet) {
		// Send immediately if sendImmediately is true
		if (sendImmediately) {
			sendPacket(packet, id);
		} else {
			// Queue it up otherwise
			try {
				outgoingQueue.put(new OutgoingPacket(id, packet));
			} catch (InterruptedException e) {
			}
		}
	}

	// Send the packet to a client
	public void sendPacket(Packet packet, long to) {
		Connection connection = connections.get(to);
		if (connection != null) {
			connection.sendPacket(packet);
		} else {
			System.out.println("Attempt to send to non-existent client: " + to);
		}
	}

	// Change the level of a player
	public void setPlayerLevel(String levelName, String playerName, long id) {
		// Create a new player
		EntityPlayer player = new EntityPlayer();
		player.id = id;
		player.playerName = playerName;
		player.level = levelName;

		// Find the level
		Level level = world.levels.get(player.level);

		// Move the player to spawn
		player.position = level.spawnLocation;

		// Send the new world to the player
		queuePacket(player.id, new PacketNewWorld(player.level, level.obj, level.mtl, level.aabbs));

		// Send all the entities in that level to the player
		for (Entity entity : world.entities.values()) {
			if (entity.level.equals(player.level)) {
				queuePacket(id, new PacketEntitySpawn(entity.id, EntityRegistry.classToId.get(entity.getClass())));
				buffer.clear();
				entity.monitor.serialize(buffer, true);
				byte[] arr = new byte[buffer.position()];
				buffer.flip();
				buffer.get(arr);
				queuePacket(id, new PacketEntityUpdate(entity.id, true, arr));
			}
		}

		// Spawn the entity in the world
		world.spawnEntity(player);

		// Tell the player to change the player entity
		queuePacket(id, new PacketEntitySetPlayer(player.id, false));

		// Force all properties to be updated on that player
		world.forceUpdate(player);
	}

	public void run() {
		// Start console scanner Loop
		new ScannerLoop().start();

		long lastTime = System.nanoTime(), timer = System.currentTimeMillis();
		double ns = 1000000000 / (double) TARGET_TPS, delta = 0;
		int tpsProc = 0;

		// Main server loop
		while (true) {
			// Process all the console commands
			while(!commandQueue.isEmpty()) {
				String command = commandQueue.poll();
				try {
					String[] splitted = command.split(" +");
					switch (splitted[0]) {
						case "tps":
							System.out.println("[Info]: Target TPS: " + TARGET_TPS + "\n[Info]: Current TPS: " + CURRENT_TPS);
							break;
						case "pingall":
							for (Connection c : connections.values()) {
								c.sendPacket(new PacketPing(true));
							}
							break;
						// Teleport player to new level
						case "tp":
							if (!world.levels.containsKey(splitted[1])) {
								System.err.println("No such level: " + splitted[1]);
								break;
							}
							for (Entity entity : world.entities.values()) {
								world.setEntityLevel(entity, splitted[1]);
							}
							break;
						// Reload a level
						case "reload":
							try {
								// Reload all the entities that are in the level
								ArrayList<Entity> reloadEntityList = new ArrayList<>();
								for(Entity entity : world.entities.values()) {
									if(entity.level.equals(splitted[1])) {
										reloadEntityList.add(entity);
									}
								}
								addLevel(splitted[1]);
								for(Entity entity : reloadEntityList) {
									world.setEntityLevel(entity, splitted[1]);
								}
								System.out.println("Reloaded");
							} catch(Exception e) {
								System.err.println("Cannot load level");
								e.printStackTrace();
							}
							break;
						// List all players
						case "list":
							for(Map.Entry<Long, Connection> conn : connections.entrySet()) {
								System.out.println(conn.getKey() + ": " + conn.getValue().socket.getInetAddress());
							}
							break;
						// Put player into spectator mode
						case "spectate":
							EntityPlayer player = ((EntityPlayer) world.entities.get(Long.parseLong(splitted[1])));
							if(splitted.length > 2) {
								player.spectate = Boolean.parseBoolean(splitted[2]);
							} else {
								player.spectate = !player.spectate;
							}
							world.forceUpdate(player);
							System.out.println("Spectating: " + player.spectate);
							break;
						// Stop the server
						case "stop":
							System.exit(0);
							break;
					}
				} catch(Exception e) {
					System.err.println("Exception occurred when processing command: " + command);
					e.printStackTrace();
				}
			}

			// Start processing packets
			sendImmediately = true;

			// Process all queued up packet
			while (!receiveQueue.isEmpty()) {
				IncomingPacket incoming = receiveQueue.poll();

				Long id = connectionsLookup.get(incoming.connection);

				Packet packet = incoming.packet;

				try {

					if (packet instanceof Event) {
						// Dummy packet
						Event p = (Event) packet;
						if (p.status == Event.CONNECT) {
							// New player, generate random player id
							id = random.nextLong() & 0x7FFFFFFFFFFFFFFFL;
							connectionsLookup.put(incoming.connection, id);
						} else if (p.status == Event.DISCONNECT) {
							// Disconnect player, kill player
							if (id != null && world.entities.containsKey(id)) {
								world.entities.get(id).dead = true;

								connections.remove(id);
								connectionsLookup.remove(incoming.connection);
							}
						}
					} else if (packet instanceof PacketPlayerJoin) {
						// Player joined, send them level
						PacketPlayerJoin p = (PacketPlayerJoin) packet;
						System.out.println("Player " + id + " joined");
						connections.put(id, incoming.connection);

						setPlayerLevel(defaultLevel, p.playerName, id);

					} else if (packet instanceof PacketPlayerInput) {
						// Player inputted, broadcast movement
						PacketPlayerInput p = (PacketPlayerInput) packet;
						Entity player = world.entities.get(id);
						player.position = new Vec3(p.x, p.y, p.z);
						player.quat = new Quat4(p.qw, p.qx, p.qy, p.qz);
					}
				} catch (Exception e) {
					System.err.println(
							"Exception occurred while processing packet: " + packet + " from " + incoming.connection);
					e.printStackTrace();
				}
			}

			sendImmediately = false;

			// Compute delta time between ticks
			long curTime = System.nanoTime();
			delta += (curTime - lastTime) / ns;
			lastTime = curTime;
			// It's time for a tick
			while (delta >= 1) {
				// Process Game Changes
				world.tick();
				tpsProc++;
				delta--;
			}

			// Send all queued up outgoing packets
			while (!outgoingQueue.isEmpty()) {
				OutgoingPacket packet = outgoingQueue.poll();
				sendPacket(packet.packet, packet.id);
			}

			// Calculate FPS and TPS
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				CURRENT_TPS = tpsProc;
				tpsProc = 0;
			}
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class ScannerLoop extends Thread {

		public void run() {
			// Server Console
			@SuppressWarnings("resource")
			Scanner s = new Scanner(System.in);
			while (true) {
				// Read a line from stdin
				String cmd = s.nextLine();
				try {
					commandQueue.put(cmd);
				} catch(InterruptedException e) {
				}
			}
		}

	}

}
