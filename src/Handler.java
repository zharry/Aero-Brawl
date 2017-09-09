import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Handler extends Thread {

	private boolean stopped = false;
	private int clientID;
	private Server server;
	private Socket socket;
	
	private InputStream in;
	private Thread inThread;
	private OutputStream out;
	private Thread outThread;

	public Handler(Socket socket, Server server, int clientID) {
		this.socket = socket;
		this.server = server;
		this.clientID = clientID;
	}

	public void run() {
		try {			
			// Create Threads for processing client data
			in = socket.getInputStream();
			inThread = new Thread (new Runnable() {
				public void run() {
					// Input Reader (From Client)
					while (!stopped) {
						try {
							in.read();
						} catch (IOException e) {
							// Client Disconnected
							try {
								removeClient();
							} catch (IOException e1) {
							}
						}
					}
				}
			});
			inThread.start();
			out = socket.getOutputStream();
			outThread = new Thread(new Runnable() {
				public void run() {
					// Output Writer (To Client)
					while (!stopped) {
						try {
							out.write(0);
						} catch (IOException e) {
							// Client Disconnected
							try {
								removeClient();
							} catch (IOException e1) {
							}
						}
					}
				}
			});
			outThread.start();
			
		} catch (IOException e) {
			System.out.println(e);
		} 
	}
	
	public void removeClient() throws IOException {
		server.removeClient(clientID);
		socket.close();
		stopped = true;
	}
}