package network.packet;

public class PacketPlayerJoin extends Packet {

	public String playerName;

	public PacketPlayerJoin(String playerName) {
		this.playerName = playerName;
	}
}
