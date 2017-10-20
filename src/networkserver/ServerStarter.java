package networkserver;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.exceptions.NNCantStartServer;
import com.jmr.wrapper.server.Server;
import packet.CreateGameObject;
import packet.MoveGameObject;
import packet.Packet;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerStarter {

	private Server server;

	private final int TARGET_TPS = 20;
	private int CURRENT_TPS = 0;

	public ServerStarter(int port) throws NNCantStartServer {

		server = new Server(port, port);
		server.setListener(new ServerListener());
		if (server.isConnected()) {
			System.out.println("Server is now listening on 0.0.0.0:" + port);

			// Server Loop
			new Thread() {
				public void run() {
					long lastTime = System.nanoTime(), timer = System.currentTimeMillis();
					double ns = 1000000000 / (double) TARGET_TPS, delta = 0;
					int tpsProc = 0;
					while (true) {
						long curTime = System.nanoTime();
						delta += (curTime - lastTime) / ns;
						lastTime = curTime;
						while (delta >= 1) {
							// Process Game Changes
							tpsProc++;
							delta--;
						}
						// Broadcast Instructions
						ArrayBlockingQueue<Packet> packets = ConnectionManager.getInstance().getPacketQueue();
						while(!packets.isEmpty()) {
							Packet packet = packets.poll();
							if(packet instanceof MoveGameObject || packet instanceof CreateGameObject) {
								ArrayList<Connection> cons = ConnectionManager.getInstance().getConnections();
								for (Connection c : cons) {
									c.sendTcp(packet);
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
			}.start();

			// Server Console
			Scanner s = new Scanner(System.in);
			while (true) {
				String cmd = s.nextLine();
				if (cmd.equals("/tps")) {
					System.out.println("[Info]: Target TPS: " + TARGET_TPS + "\n[Info]: Current TPS: " + CURRENT_TPS);
				} else if (cmd.equals("/pingall")) {
					ArrayList<Connection> cons = ConnectionManager.getInstance().getConnections();
					for (Connection c : cons) {
						c.sendTcp(new packet.Ping(true));
					}
				}
			}

		}
	}

}
