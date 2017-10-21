// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

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
