// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketDeleteEntity extends Packet {
	public long id;

	public PacketDeleteEntity(long id) {
		this.id = id;
	}
}
