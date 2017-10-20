package networkclient;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;
import packet.Packet;

public class ClientListener implements SocketListener {

	public ClientStarter starter;

	public ClientListener(ClientStarter starter) {
		this.starter = starter;
	}

	public void received(Connection con, Object object) {
		if (object instanceof packet.Ping) {
			NetworkUtil.processPing(con, (packet.Ping) object, false);
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
