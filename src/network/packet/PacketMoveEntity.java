package network.packet;

public class PacketMoveEntity extends Packet {

	public long id;

	public double x, y, z;
	public double vx, vy, vz;
	public double qw, qx, qy, qz;
	public boolean updateVelocity;
}
