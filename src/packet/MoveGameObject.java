package packet;

public class MoveGameObject extends Packet {

	public long id;

	public double x, y, z;
	public double vx, vy, vz;
	public double qw, qx, qy, qz;
	public boolean updateVelocity;
}
