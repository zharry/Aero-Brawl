package network.packet;

public class PacketPlayerInput extends Packet {

	public double x, y, z;

	public PacketPlayerInput(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
