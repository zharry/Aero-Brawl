package NetworkClient;

import java.net.ConnectException;

import com.jmr.wrapper.client.Client;

public class ClientStarter {

	Client client;

	public ClientStarter(String IP, int port) throws ConnectException  {

		client = new Client(IP, port, port);
		client.setListener(new ClientListener());
		client.connect();
		if (client.isConnected()) {
			System.out.println("Client connected to " + IP + ":" + port);
		} else {
			throw new ConnectException();
		}

	}

}
