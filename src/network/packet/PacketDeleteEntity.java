package network.packet;

public class PacketDeleteEntity extends Packet {
	public long id;

	public PacketDeleteEntity(long id) {
		this.id = id;
	}
}
