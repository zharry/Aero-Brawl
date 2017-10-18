package NetworkClient;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

public class ClientListener implements SocketListener {

	public void received(Connection con, Object object) {
		if (object instanceof Packet.Ping) {
			long diff = System.currentTimeMillis() - ((Packet.Ping) object).time;
			consoleLog(con, "Ping Recieved! Ping: " + diff + "ms");
		}

	}

	public void connected(Connection con) {
		consoleLog(con, "Connected!");
	}

	public void disconnected(Connection con) {
		consoleLog(con, "Disconnected!");
	}

	static void consoleLog(Connection con, String msg) {
		System.out.println("[Server]: " + msg);
	}

}
