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

	@Synchronize
	public double mass = 1;

	@Synchronize
	public Vec3 angVelocity = new Vec3();

	public Entity() {
	}

	public void tick(World world) {
	}

	public final FieldMonitor monitor = new FieldMonitor(this);
}
