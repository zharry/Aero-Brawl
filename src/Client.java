import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {

	InputStream in;
	OutputStream out;

	public Client() {

		// Rendering, Client UI and sending data to the server

	}

	// Prompt for server IP
	private String getServerAddress() {
		return JOptionPane.showInputDialog(null, "IP Address:", "", JOptionPane.QUESTION_MESSAGE);
	}

	public void run() throws IOException {

		// Make connection and initialize data streams
		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, 9001);
		in = socket.getInputStream();
		out = socket.getOutputStream();

		// Processes data from server
		while (true) {
		}
	}

}