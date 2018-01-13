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
	public boolean dead;
	public transient World world;

	@Synchronize
	public Vec3 position = new Vec3();

	@Synchronize
	public Vec3 velocity = new Vec3();

	@Synchronize
	public Quat4 quat = new Quat4();

	public Vec3 lastPosition = new Vec3();
	public Quat4 lastQuat = new Quat4();

	public Entity() {
	}

	public void tick() {
	}

	public void preNetwork() {
		lastPosition = position;
		lastQuat = quat;
	}

	public final FieldMonitor monitor = new FieldMonitor(this);
}
