package clientrender;

public class Vec3 {
	public double x, y, z;
	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vec3 add(Vec3 a) {
		return new Vec3(x + a.x, y + a.y, z + a.z);
	}

	public Vec3 sub(Vec3 a) {
		return new Vec3(x - a.x, y - a.y, z - a.z);
	}

	public Vec3 cross(Vec3 a) {
		return new Vec3(y * a.z - z * a.y, z * a.x - x * a.z, x * a.y - y * a.x);
	}

	public double dot(Vec3 a) {
		return x * a.x + y * a.y + z * a.z;
	}

	public double lenSq() {
		return x * x + y * y + z * z;
	}

	public double len() {
		return Math.sqrt(lenSq());
	}

	public Vec3 normalize() {
		return mul(1.0 / len());
	}

	public Vec3 mul(double fact) {
		return new Vec3(x * fact, y * fact, z * fact);
	}

	public boolean equals(Object o) {
		if(o instanceof Vec3) {
			Vec3 ot = (Vec3) o;
			return ot.x == x && ot.y == y && ot.z == z;
		}
		return false;
	}

	public String toString() {
		return "[x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}

