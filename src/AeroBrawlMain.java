import java.io.IOException;

import javax.swing.JOptionPane;

public class AeroBrawlMain {

	public static final String VERSION = "0.0.0.1";
	public static final String TITLE = "Aero Brawl";
	public static final int SERVER_PORT = 9001;

	public static void main(String[] args) throws IOException {

		// Launcher; Selector for creating a lobby room or joining an existing
		// one
		Object[] options = { "Join Game", "Create Server" };
		int select = JOptionPane.showOptionDialog(null, "Select game mode:", TITLE + " v." + VERSION,
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

		if (select == 0) {

			// Start and Run code for client
			System.out.println("Running as client.");
			Client client = new Client();
			client.start();

		} else if (select == 1) {

			// Start and Run code for server
			System.out.println("Running as server.");
			Server server = new Server();
			server.start();

		} else {
			// Close Launcher
			System.exit(0);
		}

	}

}
