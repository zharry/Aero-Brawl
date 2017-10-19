package NetworkServer;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

public class ServerListener implements SocketListener {

	public void received(Connection con, Object object) {
		if (object instanceof Packet.Ping) {
			con.sendTcp((Packet.Ping) object);
			consoleLog(con, "Ping recieved!");
		}
	}

	public void connected(Connection con) {
		consoleLog(con, "Client connected!");
	}

	public void disconnected(Connection con) {
		consoleLog(con, "Client disconnected!");

	}
	
	static void consoleLog(Connection con, String msg) {
		System.out.println("[" + con.getAddress() + "]: " + msg);
	}

}
