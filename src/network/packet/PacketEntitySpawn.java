// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketEntitySpawn extends Packet {
	public long id;
	public int entityClassId;

	public PacketEntitySpawn(long id, int entityClassId) {
		this.id = id;
		this.entityClassId = entityClassId;
	}
}
