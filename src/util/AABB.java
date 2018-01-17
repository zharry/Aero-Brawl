package util;

import util.math.CollisionFace;
import util.math.Vec3;

import java.io.Serializable;

public class AABB implements Serializable {

	public boolean collidable = true;
	public boolean renderable = true;
	public String material;

	public Vec3 min = new Vec3();
	public Vec3 max = new Vec3();

	public AABB() {
	}

	public AABB(Vec3 min, Vec3 max) {
		this.min = min;
		this.max = max;
	}

	public boolean intersect(AABB another) {
		Vec3 nmin = Util.max(min, another.min);
		Vec3 nmax = Util.min(max, another.max);

		return nmin.x <= nmax.x && nmin.y <= nmax.y && nmin.z <= nmax.z;
	}

	public AABB offset(Vec3 offset) {
		return new AABB(min.add(offset), max.add(offset));
	}

	public AABB expand(Vec3 vec) {
		Vec3 nmin = Util.min(min.add(vec), min);
		Vec3 nmax = Util.max(max.add(vec), max);
		return new AABB(nmin, nmax);
	}

	public AABB expandAmt(double amt) {
		Vec3 shift = new Vec3(amt, amt, amt);
		return new AABB(min.sub(shift), max.add(shift));
	}

	public CollisionFace[] generateCollisionFaces() {
		CollisionFace[] face = new CollisionFace[6];
		face[CollisionFace.NEGX] = new CollisionFace(CollisionFace.NEGX,
				min.x,
				min.y, max.y,
				min.z, max.z
		);
		face[CollisionFace.POSX] = new CollisionFace(CollisionFace.POSX,
				max.x,
				min.y, max.y,
				min.z, max.z
		);
		face[CollisionFace.NEGY] = new CollisionFace(CollisionFace.NEGY,
				min.y,
				min.x, max.x,
				min.z, max.z
		);
		face[CollisionFace.POSY] = new CollisionFace(CollisionFace.POSY,
				max.y,
				min.x, max.x,
				min.z, max.z
		);
		face[CollisionFace.NEGZ] = new CollisionFace(CollisionFace.NEGZ,
				min.z,
				min.x, max.x,
				min.y, max.y
		);
		face[CollisionFace.POSZ] = new CollisionFace(CollisionFace.POSZ,
				max.z,
				min.x, max.x,
				min.y, max.y
		);
		return face;
	}

	public String toString() {
		return "[min=" + min + ", max=" + max + "]";
	}
}
