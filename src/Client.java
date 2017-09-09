import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class Client {

	private int clientID;
	private Socket socket;
	
	private InputStream in;
	private Thread inThread;
	private OutputStream out;
	private Thread outThread;


	// Prompt for server IP
	private String getServerAddress() {
		return JOptionPane.showInputDialog(null, "IP Address:", "", JOptionPane.QUESTION_MESSAGE);
	}
	public Client() throws UnknownHostException, IOException {
		
		// Make connection to server
		String serverAddress = getServerAddress();
		this.socket = new Socket(serverAddress, AeroBrawlMain.SERVER_PORT);
		System.out.println("Connected to " + serverAddress + ":" + AeroBrawlMain.SERVER_PORT);

		// Rendering, Client UI and sending data to the server

	}

	public void start() throws IOException {

		// Create Threads for processing server information
		in = socket.getInputStream();
		inThread = new Thread(new Runnable() {
			public void run() {
				// Input Reader (From Server)
				while (true) {

				}
			}
		});
		inThread.start();
		out = socket.getOutputStream();
		outThread = new Thread(new Runnable() {
			public void run() {
				// Output Writer (To Server)
				while (true) {

				}
			}
		});
		outThread.start();
	}

}