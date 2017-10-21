package util.math;

public class Vec2 {
	public double x, y;
	public Vec2() {
	}
	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vec2 add(Vec2 a) {
		return new Vec2(x + a.x, y + a.y);
	}

	public Vec2 sub(Vec2 a) {
		return new Vec2(x - a.x, y - a.y);
	}

	public double cross(Vec2 a) {
		return x * a.y - y * a.x;
	}

	public double dot(Vec2 a) {
		return x * a.x + y * a.y;
	}

	public double lenSq() {
		return x * x + y * y;
	}

	public double len() {
		return Math.sqrt(lenSq());
	}

	public Vec2 mul(double fact) {
		return new Vec2(x * fact, y * fact);
	}

	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}
}
