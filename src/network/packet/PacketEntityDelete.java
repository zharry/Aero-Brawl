// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketEntityDelete extends Packet {
	private static final long serialVersionUID = 7282658972841172108L;
	public long id;

	public PacketEntityDelete(long id) {
		this.id = id;
	}
}
