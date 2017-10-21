package network.server;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;
import network.client.NetworkUtil;
import network.packet.Event;
import network.packet.Packet;
import network.packet.PacketPing;

public class ServerListener implements SocketListener {

	public ServerHandler server;

	public ServerListener(ServerHandler server) {
		this.server = server;
	}

	public void received(Connection con, Object object) {
		if (object instanceof PacketPing) {
			NetworkUtil.processPing(con, (PacketPing) object, true);
		} else if (object.toString().equals("TestAlivePing")) {
			NetworkUtil.log(con, object.toString());
		} else {
			server.packetQueue.offer(new IncomingPacket(con, (Packet) object));
		}
	}

	public void connected(Connection con) {
		NetworkUtil.log(con, "Client connected!");
		server.packetQueue.offer(new IncomingPacket(con, new Event(Event.CONNECT)));
	}

	public void disconnected(Connection con) {
		NetworkUtil.log(con, "Client disconnected!");
		server.packetQueue.offer(new IncomingPacket(con, new Event(Event.DISCONNECT)));
	}

}
