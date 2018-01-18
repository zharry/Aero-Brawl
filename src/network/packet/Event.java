// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class Event extends Packet {
	public static final int CONNECT = 1;
	public static final int DISCONNECT = 2;

	public int status;

	public Event(int status) {
		this.status = status;
	}
}
