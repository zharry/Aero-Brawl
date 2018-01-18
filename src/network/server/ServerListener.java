// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.server;

import network.Connection;
import network.ConnectionListener;
import network.packet.Event;
import network.packet.Packet;
import network.packet.PacketPing;
import util.NetworkUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener extends Thread implements ConnectionListener {

	public ServerHandler server;
	public ServerSocket serverSocket;

	public ServerListener(ServerHandler server, ServerSocket serverSocket) {
		this.server = server;
		this.serverSocket = serverSocket;
		start();
	}

	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				new Connection(this, socket);
			}
		} catch(IOException e) {
			System.err.println("Error occurred when accepting connection");
			e.printStackTrace();
		}
	}

	public void received(Connection con, Object object) {
		if (object instanceof PacketPing) {
			NetworkUtil.processPing(con, (PacketPing) object, true);
		} else if (object instanceof Packet) {
			while (true) {
				try {
					server.receiveQueue.put(new IncomingPacket(con, (Packet) object));
					break;
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void connected(Connection con) {
		NetworkUtil.log(con, "Client connected!");
		while (true) {
			try {
				server.receiveQueue.put(new IncomingPacket(con, new Event(Event.CONNECT)));
				break;
			} catch (InterruptedException e) {
			}
		}
	}

	public void disconnected(Connection con) {
		NetworkUtil.log(con, "Client disconnected!");
		while (true) {
			try {
				server.receiveQueue.put(new IncomingPacket(con, new Event(Event.DISCONNECT)));
				break;
			} catch (InterruptedException e) {
			}
		}
	}

}
