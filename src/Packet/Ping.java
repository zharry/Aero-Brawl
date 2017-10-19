package Packet;

import java.io.Serializable;

public class Ping implements Serializable {

	public long time;
	
	public Ping() {
		time = System.currentTimeMillis();
	}
	
}
