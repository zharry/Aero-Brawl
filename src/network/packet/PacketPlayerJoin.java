// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketPlayerJoin extends Packet {

	private static final long serialVersionUID = 6102831404706918429L;
	public String playerName;

	public PacketPlayerJoin(String playerName) {
		this.playerName = playerName;
	}
}
