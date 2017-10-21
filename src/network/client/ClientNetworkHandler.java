// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.client;

import com.jmr.wrapper.client.Client;
import com.jmr.wrapper.common.Connection;
import network.packet.Packet;

import java.net.ConnectException;
import java.util.concurrent.ArrayBlockingQueue;

public class ClientNetworkHandler {

	public ArrayBlockingQueue<Packet> packets = new ArrayBlockingQueue<>(4096);

	private Client client;
	private Connection connection;

	public ClientNetworkHandler(String IP, int port) throws ConnectException {

		client = new Client(IP, port, port);
		client.setListener(new ClientListener(this));
		client.connect();
		if (client.isConnected()) {
			System.out.println("Client connected to " + IP + ":" + port);
		} else {
			throw new ConnectException();
		}

		connection = client.getServerConnection();
	}

	public void sendPacket(Packet packet) {
		connection.sendTcp(packet);
	}

}
