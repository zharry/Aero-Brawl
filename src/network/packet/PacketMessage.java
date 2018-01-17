package network.packet;

public class PacketMessage extends Packet {
	public String message;

	public PacketMessage(String message) {
		this.message = message;
	}
}
