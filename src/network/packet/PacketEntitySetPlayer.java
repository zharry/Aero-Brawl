// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketEntitySetPlayer extends Packet {
	private static final long serialVersionUID = 3367697712511583896L;
	public long id;

	public boolean spectator;

	public PacketEntitySetPlayer(long id, boolean spectator) {
		this.id = id;
		this.spectator = spectator;
	}
}
