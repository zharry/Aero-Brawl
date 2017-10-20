package networkclient;

import com.jmr.wrapper.common.Connection;

public class NetworkUtil {

	public static void log(Connection con, String msg) {
		System.out.println("[" + con.getAddress() + "]: " + msg);
	}

	public static void processPing(Connection con, packet.Ping ping, boolean isServer) {
		String out = "Ping (" + (System.currentTimeMillis() - ping.getTime()) + "ms) Recieved! ";
		if ((isServer && !ping.fromServer()) || !isServer && ping.fromServer()) {
			con.sendTcp(ping);
			out += "Bounced Back.";
		}
		NetworkUtil.log(con, out);
	}

}
