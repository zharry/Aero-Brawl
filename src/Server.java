import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Server extends Thread {

	private Socket socket;
	private InputStream in;
	private OutputStream out;

	public Server(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {

			// Create players data streams
			in = socket.getInputStream();
			out = socket.getOutputStream();

			// Internal Game Logic and Processing
			while (true) {
			}
		} catch (IOException e) {
			System.out.println(e);

			// Client Disconnects Code
		} finally {
		}
	}
}