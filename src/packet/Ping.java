package packet;

public class Ping extends Packet {

	private long time;
	private boolean fromServer;

	public Ping() {
		this(false);
	}

	public Ping(boolean fromServer) {
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
