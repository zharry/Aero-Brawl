package networkserver;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

import networkclient.NetworkUtil;

public class ServerListener implements SocketListener {

	public void received(Connection con, Object object) {
		if (object instanceof packet.Ping) {
			con.sendTcp((packet.Ping) object);
			NetworkUtil.log(con, "Ping recieved!");
		}
	}

	public void connected(Connection con) {
		NetworkUtil.log(con, "Client connected!");
	}

	public void disconnected(Connection con) {
		NetworkUtil.log(con, "Client disconnected!");

	}

}
