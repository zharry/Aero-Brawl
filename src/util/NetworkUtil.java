// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package util;

import network.packet.PacketPing;
import network.Connection;

public class NetworkUtil {

	public static void log(Connection con, String msg) {
		System.out.println("[" + con.socket.getInetAddress() + "]: " + msg);
	}

	public static void processPing(Connection con, PacketPing ping, boolean isServer) {
		String out = "PacketPing (" + (System.currentTimeMillis() - ping.getTime()) + "ms) Recieved! ";
		if ((isServer && !ping.fromServer()) || !isServer && ping.fromServer()) {
			con.sendPacket(ping);
			out += "Bounced Back.";
		}
		NetworkUtil.log(con, out);
	}

}
