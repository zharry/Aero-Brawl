import java.io.IOException;

import javax.swing.JOptionPane;

import NetworkClient.ClientStarter;
import NetworkServer.ServerStarter;

public class AeroBrawlMain {

	public static final String VERSION = "0.0.0.1";
	public static final String TITLE = "Aero Brawl";
	public static final int SERVER_PORT = 9001;

	public static void main(String[] args) throws IOException {

		// Selector for creating a lobby room or joining an existing one
		int select = JOptionPane.showOptionDialog(null, "Select game mode:", TITLE + " v." + VERSION,
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
				new Object[] { "Join Game", "Create Server" }, "Join Game");

		// Join Game
		if (select == 0) {

			// Specify Connection details
			String serverIP;
			int serverPort;
			tryUntil: while (true)
				try {
					String[] serverIPPort = ((String) JOptionPane.showInputDialog(null, "Server IP:Port",
							"Connect to server", JOptionPane.PLAIN_MESSAGE, null, null, "localhost:" + SERVER_PORT))
									.split(":");
					serverIP = serverIPPort[0];
					serverPort = serverIPPort.length == 1 ? SERVER_PORT : Integer.parseInt(serverIPPort[1]);
					break tryUntil;
				} catch (Exception e) {
					displayError(1);
				}

			// Start and Run code for client
			System.out.println("Launching as Client.");
			try {
				ClientStarter client = new ClientStarter(serverIP, serverPort);
			} catch (Exception e) {
				displayError(0);
			}

			// Create Server
		} else if (select == 1) {

			// Specify Host listening port
			int serverPort;
			tryUntil: while (true)
				try {
					serverPort = Integer.parseInt((String) JOptionPane.showInputDialog(null, "Server Port",
							"Create Server", JOptionPane.PLAIN_MESSAGE, null, null, SERVER_PORT));
					break tryUntil;
				} catch (Exception e) {
					displayError(1);
				}

			// Start and Run code for server
			System.out.println("Launching as Host.");
			try {
				ServerStarter server = new ServerStarter(serverPort);
			} catch (Exception e) {
				displayError(2);
			}

			// Close Game
		} else {
			// Close Launcher
			System.exit(0);
		}

	}

	public static void displayError(int id) {
		String msg;
		switch (id) {
		case 0:
			msg = "Failed to Connect to Server!";
			break;
		case 1:
			msg = "Please follow the proper input guidelines!";
			break;
		case 2:
			msg = "Server Failed to Start. Port already in use!";
			break;
		default:
			msg = "Unknown error occured!";
		}

		int select = JOptionPane.showOptionDialog(null, msg, "Error!", JOptionPane.YES_NO_OPTION,
				JOptionPane.ERROR_MESSAGE, null, new Object[] { "Try Again", "Exit Game" }, "Try Again");
		// Exit unless user selects "Try Again"
		if (select != 0)
			System.exit(0);
	}

}
