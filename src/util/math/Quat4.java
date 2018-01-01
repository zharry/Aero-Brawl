// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package util.math;

public class Quat4 {
	public final double w, x, y, z;

	public Quat4() {
		w = x = y = z = 0;
	}

	public Quat4(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean equals(Object o) {
		if(o instanceof Quat4) {
			Quat4 ot = (Quat4) o;
			return ot.w == w && ot.x == x && ot.y == y && ot.z == z;
		}
		return false;
	}
}
