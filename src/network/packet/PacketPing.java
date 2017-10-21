// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketPing extends Packet {

	private long time;
	private boolean fromServer;

	public PacketPing() {
		this(false);
	}

	public PacketPing(boolean fromServer) {
		time = System.currentTimeMillis();
		this.fromServer = fromServer;
	}

	public long getTime() {
		return time;
	}

	public boolean fromServer() {
		return fromServer;
	}

}
