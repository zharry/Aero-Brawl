// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.client;

import network.Connection;
import network.packet.Packet;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class ClientNetworkHandler {

	public ArrayBlockingQueue<Packet> packets = new ArrayBlockingQueue<>(4096);

	private Socket socket;
	private Connection connection;

	public ClientNetworkHandler(String ip, int port) throws IOException {

		socket = new Socket(ip, port);
		connection = new Connection(new ClientListener(this), socket);
	}

	public void sendPacket(Packet packet) {
		connection.sendPacket(packet);
	}

}
