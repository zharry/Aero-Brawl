import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayDeque;
import java.util.HashMap;

public class Server {

	private ServerSocket listener;
	
	private ArrayDeque<byte[]> commandQueue;
	HashMap<Integer, Handler> clients;
	int nextClientID = 0;

	public Server() throws IOException {

		this.commandQueue = new ArrayDeque<byte[]>();
		this.clients = new HashMap<Integer, Handler>();
		this.listener = new ServerSocket(AeroBrawlMain.SERVER_PORT);
		System.out.println("Server listening on 0.0.0.0:" + AeroBrawlMain.SERVER_PORT);
	}

	public void start() throws IOException {
		while (true) {
			addClient();
		}
	}
	
	public void addClient() throws IOException {
		int clientID = nextClientID++;
		Handler client = new Handler(listener.accept(), this, clientID);
		client.start();
		clients.put(clientID, client);
		System.out.println("Client " + clientID + " has joined!");
	}
	
	public void removeClient(int clientID) {
		clients.remove(clientID);
		System.out.println("Client " + clientID + " has disconnected!");
	}
	
}