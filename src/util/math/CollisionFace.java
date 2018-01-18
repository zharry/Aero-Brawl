// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package util.math;

public class CollisionFace implements Comparable<CollisionFace> {

	public static final int
			NEGX = 0,
			NEGY = 1,
			NEGZ = 2,
			POSX = 3,
			POSY = 4,
			POSZ = 5;

	public double dist;

	public int type;

//	public Vec3 center = new Vec3();

	public double coord1;

	public double coord2a1, coord2a2;
	public double coord2b1, coord2b2;

	public CollisionFace(int type, double coord1, double coord2a1, double coord2a2, double coord2b1, double coord2b2) {
		this.type = type;
		this.coord1 = coord1;
		this.coord2a1 = coord2a1;
		this.coord2a2 = coord2a2;
		this.coord2b1 = coord2b1;
		this.coord2b2 = coord2b2;
	}

//	public void compute() {
//		double coord2a = (coord2a1 + coord2a2) / 2;
//		double coord2b = (coord2b1 + coord2b2) / 2;
//		switch(type % 3) {
//			case NEGX:
//				center = new Vec3(coord1, coord2a, coord2b);
//				break;
//			case NEGY:
//				center = new Vec3(coord2a, coord1, coord2b);
//				break;
//			case NEGZ:
//				center = new Vec3(coord2a, coord2b, coord1);
//		}
//		dist = center.sub(distTo).len();
//	}
//
//	public void shift(Vec3 v) {
//		switch(type % 3) {
//			case NEGX:
//				coord1 += v.x;
//				coord2a1 += v.y; coord2a2 += v.y;
//				coord2b1 += v.z; coord2b2 += v.z;
//				break;
//			case NEGY:
//				coord1 += v.y;
//				coord2a1 += v.x; coord2a2 += v.x;
//				coord2b1 += v.z; coord2b2 += v.z;
//				break;
//			case NEGZ:
//				coord1 += v.z;
//				coord2a1 += v.x; coord2a2 += v.x;
//				coord2b1 += v.y; coord2b2 += v.y;
//				break;
//		}
//	}

	public int compareTo(CollisionFace face) {
		return Double.compare(dist, face.dist);
	}

	public double collide(CollisionFace other, Vec3 velOther) {
		if(type != (other.type + 3) % 6)
			return Double.NaN;
		double velMain;
		double vel1;
		double vel2;
		switch(type % 3) {
			case NEGX:
				velMain = velOther.x;
				vel1 = velOther.y;
				vel2 = velOther.z;
				break;
			case NEGY:
				velMain = velOther.y;
				vel1 = velOther.x;
				vel2 = velOther.z;
				break;
			case NEGZ:
				velMain = velOther.z;
				vel1 = velOther.x;
				vel2 = velOther.y;
				break;
			default:
				return Double.NaN;
		}

		if(Math.abs(velMain) < 1e-9)
			return Double.NaN;

		double timeDiff = (coord1 - other.coord1) / velMain;

		if(!(0 <= timeDiff && timeDiff <= 1)) {
			return Double.NaN;
		}

		double min1 = other.coord2a1 + vel1 * timeDiff;
		double max1 = other.coord2a2 + vel1 * timeDiff;
		double min2 = other.coord2b1 + vel2 * timeDiff;
		double max2 = other.coord2b2 + vel2 * timeDiff;

		if(min1 <= coord2a2 && coord2a1 <= max1 && min2 <= coord2b2 && coord2b1 <= max2) {
			return timeDiff;
		}

		return Double.NaN;

	}
}
