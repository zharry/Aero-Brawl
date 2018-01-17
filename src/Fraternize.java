// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

import client.ClientHandler;
import com.jmr.wrapper.common.exceptions.NNCantStartServer;
import network.server.ServerHandler;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;

public class Fraternize {

	public static final String VERSION = "0.0.0.1";
	public static final String TITLE = "Fraternize";
	public static final int SERVER_PORT = 9001;

	public static void main(String[] args) throws IOException {

		if (args.length != 0) {
			if (args[0].equals("nogui"))
				try {
					runServer(SERVER_PORT);
				} catch (Exception e) {
					e.printStackTrace();
				}

		} else {

			// Selector for creating a lobby room or joining an existing one
			int select = JOptionPane.showOptionDialog(null, "Select game mode:", TITLE + " v." + VERSION,
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
					new Object[] { "Join Game", "Create Server" }, "Join Game");

			// Join Game
			if (select == 0) {

				// Specify Connection details
				String serverIP;
				int serverPort;
				while (true) {
					try {
						String[] serverIPPort = ((String) JOptionPane.showInputDialog(null, "Server IP:Port",
								"Connect to server", JOptionPane.PLAIN_MESSAGE, null, null, "localhost")).split(":");
						serverIP = serverIPPort[0];
						serverPort = serverIPPort.length == 1 ? SERVER_PORT : Integer.parseInt(serverIPPort[1]);
						break;
					} catch (Exception e) {
						e.printStackTrace();
						displayError(1);
					}
				}

				// Start and Run code for client
				System.out.println("Launching as Client.");
				while (true) {
					try {
						runClient(serverIP, serverPort);
						break;
					} catch (Exception e) {
						e.printStackTrace();
						displayError(0);
					}
				}

				// Create Server
			} else if (select == 1) {

				// Specify Host listening port
				int serverPort;
				while (true) {
					try {
						serverPort = Integer.parseInt((String) JOptionPane.showInputDialog(null, "Server Port",
								"Create Server", JOptionPane.PLAIN_MESSAGE, null, null, SERVER_PORT));
						break;
					} catch (Exception e) {
						e.printStackTrace();
						displayError(1);
					}
				}

				// Start and Run code for server
				System.out.println("Launching as Host.");
				while (true) {
					try {
						runServer(serverPort);
						break;
					} catch (Exception e) {
						e.printStackTrace();
						displayError(2);
					}
				}

				// Close Game
			} else {
				// Close Launcher
				System.exit(0);
			}
		}

	}

	public static void runClient(String serverIP, int serverPort) throws ConnectException {
		ClientHandler client = new ClientHandler("TestUser", serverIP, serverPort);
		client.run();
	}

	public static void runServer(int serverPort) throws NNCantStartServer, InterruptedException {
		ServerHandler server = new ServerHandler(serverPort);
		server.run();
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