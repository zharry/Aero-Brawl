// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketEntitySpawn extends Packet {
	private static final long serialVersionUID = 8766787807554641760L;
	public long id;
	public int entityClassId;

	public PacketEntitySpawn(long id, int entityClassId) {
		this.id = id;
		this.entityClassId = entityClassId;
	}
}
