// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.server;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.exceptions.NNCantStartServer;
import com.jmr.wrapper.server.ConnectionManager;
import com.jmr.wrapper.server.Server;
import entity.Entity;
import entity.EntityPlayer;
import entity.EntityRegistry;
import network.packet.*;
import util.math.Quat4;
import util.math.Vec3;
import world.Level;
import world.WorldServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerHandler {

	public Random random = new Random();

	public WorldServer world;

	private Server server;

	private final int TARGET_TPS = 30;
	private int CURRENT_TPS = 0;

	private boolean sendImmediately = false;

	public String defaultLevel = "level1_Welcome";

	private HashMap<Long, Connection> connections = new HashMap<>();
	private HashMap<Connection, Long> connectionsLookup = new HashMap<>();

	public ArrayBlockingQueue<IncomingPacket> receiveQueue = new ArrayBlockingQueue<>(4096);
	public ArrayBlockingQueue<OutgoingPacket> outgoingQueue = new ArrayBlockingQueue<>(4096);

	private ByteBuffer buffer = ByteBuffer.allocate(65536);

	public ServerHandler(int port) throws NNCantStartServer {

		server = new Server(port, port);
		server.setListener(new ServerListener(this));
		if (server.isConnected()) {
			System.out.println("Server is now listening on 0.0.0.0:" + port);
		}

		world = new WorldServer(this);

		try {
			addLevel(defaultLevel);
		} catch (IOException e) {
			System.err.println("Cannot load level file");
			e.printStackTrace();
		}
	}

	public void addLevel(String levelName) throws IOException {
		Level level = new Level(levelName, world);
		level.loadLevelFromFile();
		world.levels.put(levelName, level);
	}

	public void queueBroadcast(String level, Packet packet) {
		for (Map.Entry<Long, Connection> conn : connections.entrySet()) {
			if (world.entities.get(conn.getKey()).level.equals(level)) {
				if (sendImmediately) {
					sendPacket(packet, conn.getKey());
				} else {
					try {
						outgoingQueue.put(new OutgoingPacket(conn.getKey(), packet));
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	public void queuePacket(long id, Packet packet) {
		if (sendImmediately) {
			sendPacket(packet, id);
		} else {
			try {
				outgoingQueue.put(new OutgoingPacket(id, packet));
			} catch (InterruptedException e) {
			}
		}
	}

	public void sendPacket(Packet packet, long to) {
		Connection connection = connections.get(to);
		if (connection != null) {
			if (packet instanceof PacketNewWorld) {
				connection.sendComplexObjectTcp(packet);
			} else {
				connection.sendTcp(packet);
			}
		} else {
			System.out.println("Attempt to send to non-existent client: " + to);
		}
	}

	public void setPlayerLevel(String levelName, String playerName, long id) {
		EntityPlayer player = new EntityPlayer();
		player.id = id;
		player.playerName = playerName;
		player.level = levelName;

		Level level = world.levels.get(player.level);

		player.position = level.spawnLocation;

		sendPacket(new PacketNewWorld(player.level, level.obj, level.mtl, level.aabbs), player.id);

		for(Entity entity : world.entities.values()) {
			if(entity.level.equals(player.level)) {
				sendPacket(new PacketEntitySpawn(entity.id, EntityRegistry.classToId.get(entity.getClass())), id);
				buffer.clear();
				entity.monitor.serialize(buffer, true);
				byte[] arr = new byte[buffer.position()];
				buffer.flip();
				buffer.get(arr);
				sendPacket(new PacketEntityUpdate(entity.id, true, arr), id);
			}
		}

		world.spawnEntity(player);

		sendPacket(new PacketEntitySetPlayer(player.id, false), id);

		world.forceUpdate(player);
	}

	public void run() {
		// Console scanner Loop
		new ScannerLoop().start();

		long lastTime = System.nanoTime(), timer = System.currentTimeMillis();
		double ns = 1000000000 / (double) TARGET_TPS, delta = 0;
		int tpsProc = 0;
		while (true) {

			sendImmediately = true;
			// Receive Input
			while (!receiveQueue.isEmpty()) {
				IncomingPacket incoming = receiveQueue.poll();

				Long id = connectionsLookup.get(incoming.connection);

				Packet packet = incoming.packet;

				try {

					if (packet instanceof Event) {
						Event p = (Event) packet;
						if (p.status == Event.CONNECT) {
							id = random.nextLong();
							connectionsLookup.put(incoming.connection, id);
						} else if (p.status == Event.DISCONNECT) {

							world.entities.get(id).dead = true;

							connections.remove(id);
							connectionsLookup.remove(incoming.connection);
						}
					} else if (packet instanceof PacketPlayerJoin) {
						PacketPlayerJoin p = (PacketPlayerJoin) packet;
						connections.put(id, incoming.connection);

						setPlayerLevel(defaultLevel, p.playerName, id);

					} else if (packet instanceof PacketPlayerInput) {
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

			long curTime = System.nanoTime();
			delta += (curTime - lastTime) / ns;
			lastTime = curTime;
			while (delta >= 1) {
				// Process Game Changes
				world.tick();
				tpsProc++;
				delta--;
			}

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
			Scanner s = new Scanner(System.in);
			while (true) {
				String cmd = s.nextLine();
				switch (cmd) {
				case "tps":
					System.out.println("[Info]: Target TPS: " + TARGET_TPS + "\n[Info]: Current TPS: " + CURRENT_TPS);
					break;
				case "pingall":
					ArrayList<Connection> cons = ConnectionManager.getInstance().getConnections();
					for (Connection c : cons)
						c.sendTcp(new PacketPing(true));
					break;
				case "stop":
				    System.exit(0);
					break;
				default:
					break;
				}
			}
		}

	}

}
