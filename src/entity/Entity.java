// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package entity;

import util.math.Quat4;
import util.math.Vec3;
import world.World;

// An entity
public abstract class Entity {

	public long id;
	public boolean dead;

	// World and level the player is in
	public World world;
	public String level;

	// Synchronize this across all clients
	@Synchronize
	public Vec3 position = new Vec3();

	// Synchronize this across all clients
	@Synchronize
	public Vec3 velocity = new Vec3();

	// Synchronize this across all clients
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

	// A field monitor to monitor when field changes
	public final FieldMonitor monitor = new FieldMonitor(this);
}
