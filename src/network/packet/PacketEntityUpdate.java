// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketEntityUpdate extends Packet {

	private static final long serialVersionUID = -4034881404794001381L;
	public long id;
	public boolean force;
	public byte[] entityUpdateData;

	public PacketEntityUpdate(long id, boolean force, byte[] entityUpdateData) {
		this.id = id;
		this.force = force;
		this.entityUpdateData = entityUpdateData;
	}

}
