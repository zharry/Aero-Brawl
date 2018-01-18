// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

public class PacketPlayerInput extends Packet {

	private static final long serialVersionUID = -1078255662234793214L;
	public double x, y, z;
	public double qw, qx, qy, qz;

	public PacketPlayerInput(double x, double y, double z, double qw, double qx, double qy, double qz) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.qw = qw;
		this.qx = qx;
		this.qy = qy;
		this.qz = qz;
	}
}
