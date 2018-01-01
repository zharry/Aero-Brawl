package network.packet;

public class PacketEntitySetPlayer extends Packet {
	public long id;

	public boolean spectator;

	public PacketEntitySetPlayer(long id, boolean spectator) {
		this.id = id;
		this.spectator = spectator;
	}
}
