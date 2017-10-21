// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package util;

import com.jmr.wrapper.common.Connection;
import network.packet.PacketPing;

public class NetworkUtil {

	public static void log(Connection con, String msg) {
		System.out.println("[" + con.getAddress() + "]: " + msg);
	}

	public static void processPing(Connection con, PacketPing ping, boolean isServer) {
		String out = "PacketPing (" + (System.currentTimeMillis() - ping.getTime()) + "ms) Recieved! ";
		if ((isServer && !ping.fromServer()) || !isServer && ping.fromServer()) {
			con.sendTcp(ping);
			out += "Bounced Back.";
		}
		NetworkUtil.log(con, out);
	}

}
