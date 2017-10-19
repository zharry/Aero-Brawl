package NetworkClient;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

public class ClientListener implements SocketListener {

	public void received(Connection con, Object object) {
		if (object instanceof Packet.Ping) {
			long diff = System.currentTimeMillis() - ((Packet.Ping) object).time;
			NetworkUtil.log(con, "Ping Recieved! Ping: " + diff + "ms");
		}

	}

	public void connected(Connection con) {
		NetworkUtil.log(con, "Connected!");
	}

	public void disconnected(Connection con) {
		NetworkUtil.log(con, "Disconnected!");
	}

}
