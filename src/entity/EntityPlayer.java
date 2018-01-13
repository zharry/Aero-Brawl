// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package entity;

import util.math.Vec3;

public class EntityPlayer extends Entity {

	public String playerName;

	public EntityPlayer() {
	}

	@Override
	public void tick() {
		super.tick();
		if(world.isClient) {
			position = position.add(velocity);
			if(position.y < 1) {
				position = new Vec3(position.x, 1, position.z);
				velocity = new Vec3(velocity.x, 0, velocity.z);
			}
			velocity = velocity.add(new Vec3(0, -0.05, 0));
			velocity = velocity.mul(0.98);
		}
	}
}
