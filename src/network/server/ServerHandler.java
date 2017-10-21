// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

// Previously ServerStarter

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
import world.World;
import world.WorldServer;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerHandler {

	public Random random = new Random();

	public World world;

	private Server server;

	private final int TARGET_TPS = 30;
	private int CURRENT_TPS = 0;

	private HashMap<Long, Connection> connections = new HashMap<>();
	private HashMap<Connection, Long> connectionsLookup = new HashMap<>();

	public ArrayBlockingQueue<IncomingPacket> packetQueue = new ArrayBlockingQueue<>(4096);

	public ServerHandler(int port) throws NNCantStartServer {

		world = new WorldServer();

		server = new Server(port, port);
		server.setListener(new ServerListener(this));
		if (server.isConnected()) {
			System.out.println("Server is now listening on 0.0.0.0:" + port);
		}

	}

	public void broadcastPacket(Packet packet) {
		for(Connection connection : connections.values()) {
			connection.sendTcp(packet);
		}
	}

	public void broadcastPacketExcept(Packet packet, long except) {
		for(Map.Entry<Long, Connection> entries : connections.entrySet()) {
			if(entries.getKey() != except) {
				entries.getValue().sendTcp(packet);
			}
		}
	}

	public void sendPacket(Packet packet, long to) {
		Connection connection = connections.get(to);
		if(connection != null) {
			connection.sendTcp(packet);
		} else {
			System.out.println("Attempt to send to non-existent client: " + to);
		}
	}

	public void run() {
		// Server Loop
		new ServerLoop().start();

		// Server Console
		Scanner s = new Scanner(System.in);
		while (true) {
			String cmd = s.nextLine();
			if (cmd.equals("/tps")) {
				System.out.println("[Info]: Target TPS: " + TARGET_TPS + "\n[Info]: Current TPS: " + CURRENT_TPS);
			} else if (cmd.equals("/pingall")) {
				ArrayList<Connection> cons = ConnectionManager.getInstance().getConnections();
				for (Connection c : cons) {
					c.sendTcp(new PacketPing(true));
				}
			}
		}
	}

	public class ServerLoop extends Thread {

		public void run() {
			long lastTime = System.nanoTime(), timer = System.currentTimeMillis();
			double ns = 1000000000 / (double) TARGET_TPS, delta = 0;
			int tpsProc = 0;
			while (true) {

				// Receive Input
				while(!packetQueue.isEmpty()) {
					IncomingPacket incoming = packetQueue.poll();

					Long id = connectionsLookup.get(incoming.connection);

					Packet packet = incoming.packet;
					if(packet instanceof Event) {
						Event p = (Event) packet;
						if(p.status == Event.CONNECT) {
							id = random.nextLong();
							connectionsLookup.put(incoming.connection, id);
						} else if(p.status == Event.DISCONNECT) {
							if(id != null) {
								broadcastPacket(new PacketDeleteEntity(id));
							}
							world.entities.remove(id);
							connections.remove(id);
							connectionsLookup.remove(incoming.connection);
						}
					} else if(packet instanceof PacketPlayerJoin) {
						PacketPlayerJoin p = (PacketPlayerJoin) packet;
						connections.put(id, incoming.connection);
						EntityPlayer player = new EntityPlayer();
						player.id = id;
						player.playerName = p.playerName;

						for(Entity entity : world.entities.values()) {
							sendPacket(new PacketCreateEntity(entity.id, entity.type), id);
							PacketMoveEntity move = new PacketMoveEntity();
							move.id = entity.id;
							move.x = entity.position.x;
							move.y = entity.position.y;
							move.z = entity.position.z;
							sendPacket(move, id);
						}

						// TODO move this into world
						world.entities.put(id, player);

						broadcastPacketExcept(new PacketCreateEntity(id, EntityRegistry.REGISTRY_EntityPlayer), id);
						sendPacket(new PacketCreateEntity(id, EntityRegistry.REGISTRY_EntityPlayer, true), id);

					} else if(packet instanceof PacketPlayerInput) {
						PacketPlayerInput p = (PacketPlayerInput) packet;
						Entity player = world.entities.get(id);
						player.position = new Vec3(p.x, p.y, p.z);
						player.quat = new Quat4(p.qw, p.qx, p.qy, p.qz);
					}
				}

				long curTime = System.nanoTime();
				delta += (curTime - lastTime) / ns;
				lastTime = curTime;
				while (delta >= 1) {
					// Process Game Changes
					world.updatePrevPos();
					world.tick();
					tpsProc++;
					delta--;
				}

				for(Entity entity : world.entities.values()) {
					if(!entity.lastPosition.equals(entity.position) || !entity.lastQuat.equals(entity.quat)) {

						PacketMoveEntity move = new PacketMoveEntity();
						move.id = entity.id;
						move.x = entity.position.x;
						move.y = entity.position.y;
						move.z = entity.position.z;
						move.qw = entity.quat.w;
						move.qx = entity.quat.x;
						move.qy = entity.quat.y;
						move.qz = entity.quat.z;

						if(entity instanceof EntityPlayer) {
							broadcastPacketExcept(move, entity.id);
						} else {
							broadcastPacket(move);
						}
					}
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

	}

}
