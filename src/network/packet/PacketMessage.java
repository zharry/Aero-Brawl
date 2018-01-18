package network.packet;

public class PacketMessage extends Packet {
	private static final long serialVersionUID = -1747441494598896430L;
	public String message;

	public PacketMessage(String message) {
		this.message = message;
	}
}
