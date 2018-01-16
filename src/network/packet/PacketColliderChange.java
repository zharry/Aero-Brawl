package network.packet;

public class PacketColliderChange extends Packet {

	public String colliderName;
	public boolean state;

	public PacketColliderChange(String colliderName, boolean state) {
		this.colliderName = colliderName;
		this.state = state;
	}
}
