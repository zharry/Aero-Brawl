package NetworkServer;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

import NetworkClient.NetworkUtil;

public class ServerListener implements SocketListener {

	public void received(Connection con, Object object) {
		if (object instanceof Packet.Ping) {
			con.sendTcp((Packet.Ping) object);
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
