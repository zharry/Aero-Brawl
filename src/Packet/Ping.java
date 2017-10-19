package Packet;

public class Ping extends Packet {

	public long time;

	public Ping() {
		time = System.currentTimeMillis();
	}

}
