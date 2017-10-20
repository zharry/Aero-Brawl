package networkserver;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

import networkclient.NetworkUtil;

public class ServerListener implements SocketListener {

	public void received(Connection con, Object object) {
		if (object instanceof packet.Ping) {
			NetworkUtil.processPing(con, (packet.Ping) object, true);
		} else if (object.toString().equals("TestAlivePing")) {
			NetworkUtil.log(con, object.toString());
		} else {
			ConnectionManager.getInstance().addPacket((packet.Packet) object);
		}
	}

	public void connected(Connection con) {
		NetworkUtil.log(con, "Client connected!");
		ConnectionManager.getInstance().addCon(con);
	}

	public void disconnected(Connection con) {
		NetworkUtil.log(con, "Client disconnected!");
		ConnectionManager.getInstance().removeCon(con);
	}

}
