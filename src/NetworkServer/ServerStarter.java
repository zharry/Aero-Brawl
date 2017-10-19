package NetworkServer;

import com.jmr.wrapper.common.exceptions.NNCantStartServer;
import com.jmr.wrapper.server.Server;

public class ServerStarter {

	private Server server;

	public ServerStarter(int port) throws NNCantStartServer {

		server = new Server(port, port);
		server.setListener(new ServerListener());
		if (server.isConnected()) {
			System.out.println("Server is now listening on 0.0.0.0:" + port);
		}
	}

}
