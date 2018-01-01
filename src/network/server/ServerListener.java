// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.server;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

import network.packet.Event;
import network.packet.Packet;
import network.packet.PacketPing;
import util.NetworkUtil;

public class ServerListener implements SocketListener {

	public ServerHandler server;

	public ServerListener(ServerHandler server) {
		this.server = server;
	}

	public void received(Connection con, Object object) {
		if (object instanceof PacketPing) {
			NetworkUtil.processPing(con, (PacketPing) object, true);
		} else if (object instanceof Packet) {
			while (true) {
				try {
					server.receiveQueue.put(new IncomingPacket(con, (Packet) object));
					break;
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void connected(Connection con) {
		NetworkUtil.log(con, "Client connected!");
		while (true) {
			try {
				server.receiveQueue.put(new IncomingPacket(con, new Event(Event.CONNECT)));
				break;
			} catch (InterruptedException e) {
			}
		}
	}

	public void disconnected(Connection con) {
		NetworkUtil.log(con, "Client disconnected!");
		while (true) {
			try {
				server.receiveQueue.put(new IncomingPacket(con, new Event(Event.DISCONNECT)));
				break;
			} catch (InterruptedException e) {
			}
		}
	}

}
