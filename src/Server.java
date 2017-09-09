import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public Server() throws IOException {

		ServerSocket listener = new ServerSocket(AeroBrawlMain.SERVER_PORT);
		System.out.println("Server started on localhost:" + AeroBrawlMain.SERVER_PORT);

		try {
			while (true) {
				Socket socket = listener.accept();
				try {
					// Server
				} finally {
					socket.close();
				}
			}
		} finally {
			listener.close();
		}
	}

}
