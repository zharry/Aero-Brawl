// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

// Previously GameObject

package entity;

import util.math.Quat4;
import util.math.Vec3;
import world.World;

public abstract class Entity {

	public long id;
	public int type;

	public Vec3 position = new Vec3();
	public Vec3 lastPosition = new Vec3();
	public Vec3 velocity = new Vec3();

	public Quat4 quat = new Quat4();
	public Quat4 lastQuat = new Quat4();

	public Entity() {
	}

	public Entity(long id) {
		this.id = id;
	}

	public void tick(World world) {
	}

}
