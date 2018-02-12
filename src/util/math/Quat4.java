// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package util.math;

// A quaternion to represent rotation
public class Quat4 {
	public final double w, x, y, z;

	public Quat4() {
		w = 1;
		x = y = z = 0;
	}

	public Quat4(double angle, Vec3 axis) {
		angle = Math.toRadians(angle / 2);
		double sa = Math.sin(angle);
		w = Math.cos(angle);
		x = axis.x * sa;
		y = axis.y * sa;
		z = axis.z * sa;
	}

	public Quat4(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Hamiltonian product
	public Quat4 prod(Quat4 q) {
		return new Quat4(
				w * q.w - x * q.x - y * q.y - z * q.z,
				w * q.x + x * q.w + y * q.z - z * q.y,
				w * q.y - x * q.z + y * q.w + z * q.x,
				w * q.z + x * q.y - y * q.x + z * q.w
		);
	}

	// Apply quaternion
	public Vec3 apply(Vec3 v) {
		Quat4 res = prod(new Quat4(0, v.x, v.y, v.z)).prod(conj());
		return new Vec3(res.x, res.y, res.z);
	}

	// Normalize quaternion
	public Quat4 normalize() {
		double m = 1.0 / len();
		return new Quat4(w * m, x * m, y * m, z * m);
	}

	public double lenSq() {
		return w * w + x * x + y * y + z * z;
	}

	public double len() {
		return Math.sqrt(lenSq());
	}

	public Quat4 conj() {
		return new Quat4(w, -x, -y, -z);
	}

	public Quat4 mul(double fact) {
		return new Quat4(w * fact, x * fact, y * fact, z * fact);
	}

	public Quat4 add(Quat4 q) {
		return new Quat4(w + q.w, x + q.x, y + q.y, z + q.z);
	}

	public Quat4 sub(Quat4 q) {
		return new Quat4(w - q.w, x - q.x, y - q.y, z - q.z);
	}

	public boolean equals(Object o) {
		if(o instanceof Quat4) {
			Quat4 ot = (Quat4) o;
			return ot.w == w && ot.x == x && ot.y == y && ot.z == z;
		}
		return false;
	}
}
