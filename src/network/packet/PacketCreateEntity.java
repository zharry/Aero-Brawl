package network.packet;

public class PacketCreateEntity extends Packet {
	public long id;
	public int objType;

	public boolean isPlayer;

	public PacketCreateEntity(long id, int objType) {
		this.id = id;
		this.objType = objType;
	}

	public PacketCreateEntity(long id, int objType, boolean isPlayer) {
		this(id, objType);
		this.isPlayer = isPlayer;
	}
}
