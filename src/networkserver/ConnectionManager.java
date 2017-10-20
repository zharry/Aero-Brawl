package networkserver;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import com.jmr.wrapper.common.Connection;

public class ConnectionManager {

	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private ArrayBlockingQueue<packet.Packet> packetQueue = new ArrayBlockingQueue<packet.Packet>(4096);

	public void addCon(Connection c) {
		connections.add(c);
	}

	public void removeCon(Connection c) {
		connections.remove(c);
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public void addPacket(packet.Packet c) {
		packetQueue.add(c);
	}

	public void clearPacketQueue(packet.Packet c) {
		packetQueue.remove(c);
	}

	public ArrayBlockingQueue<packet.Packet> getPacketQueue() {
		return packetQueue;
	}

	private static ConnectionManager instance = new ConnectionManager();

	private ConnectionManager() {
	}

	public static ConnectionManager getInstance() {
		return instance;
	}

}
