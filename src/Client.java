import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {

	InputStream in;
	Thread inThread;
	OutputStream out;
	Thread outThread;

	public Client() {

		// Rendering, Client UI and sending data to the server

	}

	// Prompt for server IP
	private String getServerAddress() {
		return JOptionPane.showInputDialog(null, "IP Address:", "", JOptionPane.QUESTION_MESSAGE);
	}

	public void run() throws IOException {

		// Make connection to server
		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, AeroBrawlMain.SERVER_PORT);

		// Create Threads for processing server information
		in = socket.getInputStream();
		inThread = new Thread(new Runnable() {
			public void run() {
				// Input Reader
				while (true) {
					
				}
			}
		});
		inThread.start();
		out = socket.getOutputStream();
		outThread = new Thread(new Runnable() {
			public void run() {
				// Output Writer
				while (true) {
					
				}
			}
		});
		outThread.start();
	}

}