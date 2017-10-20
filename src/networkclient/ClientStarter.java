package networkclient;

import com.jmr.wrapper.client.Client;
import packet.Packet;

import java.net.ConnectException;
import java.util.concurrent.ArrayBlockingQueue;

public class ClientStarter {

	public ArrayBlockingQueue<Packet> packets = new ArrayBlockingQueue<>(4096);

	public Client client;

	public ClientStarter(String IP, int port) throws ConnectException {

		client = new Client(IP, port, port);
		client.setListener(new ClientListener(this));
		client.connect();
		if (client.isConnected()) {
			System.out.println("Client connected to " + IP + ":" + port);
		} else {
			throw new ConnectException();
		}

	}

}
