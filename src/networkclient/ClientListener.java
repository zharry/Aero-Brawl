package networkclient;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

public class ClientListener implements SocketListener {

	public void received(Connection con, Object object) {
		if (object instanceof packet.Ping) {
			NetworkUtil.processPing(con, (packet.Ping) object, false);
		} else if (object.toString().equals("TestAlivePing")) {
			NetworkUtil.log(con, object.toString());
		} else {
			
		}
	}

	public void connected(Connection con) {
		NetworkUtil.log(con, "Connected!");
	}

	public void disconnected(Connection con) {
		NetworkUtil.log(con, "Disconnected!");
	}

}
