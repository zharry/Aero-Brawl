// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

// Packet for sending client a message (server -> client)
public class PacketMessage extends Packet {
	private static final long serialVersionUID = -1747441494598896430L;
	public String message;

	public PacketMessage(String message) {
		this.message = message;
	}
}
