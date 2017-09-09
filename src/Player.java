import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class Player {
	public Player() throws UnknownHostException, IOException {

		String serverAddress = JOptionPane.showInputDialog("IP Address:");
		Socket s = new Socket(serverAddress, AeroBrawlMain.SERVER_PORT);

		// Client

	}
}
