// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketPing extends Packet {

	private static final long serialVersionUID = -7753893073579570084L;
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
