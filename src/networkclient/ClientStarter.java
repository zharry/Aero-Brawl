package networkclient;

import java.net.ConnectException;
import java.util.Scanner;

import com.jmr.wrapper.client.Client;

public class ClientStarter {

	Client client;

	public ClientStarter(String IP, int port) throws ConnectException  {

		client = new Client(IP, port, port);
		client.setListener(new ClientListener());
		client.connect();
		if (client.isConnected()) {
			System.out.println("Client connected to " + IP + ":" + port);
			
			Scanner s = new Scanner(System.in);
			while(true) {
				String str = s.nextLine();
				if (str.equals("Ping")) {
					client.getServerConnection().sendTcp(new packet.Ping());
				}
			}
			
		} else {
			throw new ConnectException();
		}

	}

}
