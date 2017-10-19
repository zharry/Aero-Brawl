package packet;

public class Ping extends Packet {

	private long time;

	public Ping() {
		time = System.currentTimeMillis();
	}

	public long getTime() {
		return time;
	}

}
