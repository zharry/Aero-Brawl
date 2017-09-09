import java.io.IOException;

import javax.swing.JOptionPane;

public class AeroBrawlMain {

	public static final String VERSION = "0.0.0.1";
	public static final String TITLE = "Aero Brawl";
	public static final int SERVER_PORT = 9001;
	
	public static void main(String[] args) throws IOException {
		
		Object[] options = { "Join Game", "Create Server" };
		int select = JOptionPane.showOptionDialog(null, "Select game mode:",
				TITLE + " v." + VERSION, JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		
		switch (select) {
		case 0:
			System.out.println("Running as client.");
			Player client = new Player();
			break;
		case 1:
			System.out.println("Running as server.");
			Server server = new Server();
			break;
		default:
			System.exit(0);
		}
		
	}

}
