// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketEntityUpdate extends Packet {

	public long id;
	public boolean force;
	public byte[] entityUpdateData;

	public PacketEntityUpdate(long id, boolean force, byte[] entityUpdateData) {
		this.id = id;
		this.force = force;
		this.entityUpdateData = entityUpdateData;
	}

}
