// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

// Packet for deleting entities (server -> client)
public class PacketEntityDelete extends Packet {
	private static final long serialVersionUID = 7282658972841172108L;
	public long id;

	public PacketEntityDelete(long id) {
		this.id = id;
	}
}
