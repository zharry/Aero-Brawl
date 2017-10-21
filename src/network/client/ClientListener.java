package network.client;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;
import network.packet.Packet;
import network.packet.PacketPing;

public class ClientListener implements SocketListener {

	public ClientNetworkHandler starter;

	public ClientListener(ClientNetworkHandler starter) {
		this.starter = starter;
	}

	public void received(Connection con, Object object) {
		if (object instanceof PacketPing) {
			NetworkUtil.processPing(con, (PacketPing) object, false);
		} else if (object instanceof String) {
			NetworkUtil.log(con, object.toString());
		} else {
			while(true) {
				try {
					starter.packets.put((Packet) object);
					break;
				} catch(InterruptedException e) {
				}
			}
		}
	}

	public void connected(Connection con) {
		NetworkUtil.log(con, "Connected!");
	}

	public void disconnected(Connection con) {
		NetworkUtil.log(con, "Disconnected!");
	}

}
