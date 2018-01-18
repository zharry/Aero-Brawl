// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.client;

import network.Connection;
import network.ConnectionListener;
import network.packet.Packet;
import network.packet.PacketPing;
import util.NetworkUtil;

public class ClientListener implements ConnectionListener {

	public ClientNetworkHandler starter;

	public ClientListener(ClientNetworkHandler starter) {
		this.starter = starter;
	}

	public void received(Connection con, Object object) {
		if (object instanceof PacketPing) {
			NetworkUtil.processPing(con, (PacketPing) object, false);
		} else if (object instanceof Packet) {
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
