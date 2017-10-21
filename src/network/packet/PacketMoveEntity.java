// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketMoveEntity extends Packet {

	public long id;

	public double x, y, z;
	public double vx, vy, vz;
	public double qw, qx, qy, qz;
	public boolean updateVelocity;
}
