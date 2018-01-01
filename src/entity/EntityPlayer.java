// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package entity;

import world.World;

public class EntityPlayer extends Entity {

	public String playerName;

	public EntityPlayer() {
	}

	@Override
	public void tick(World world) {
		position = position.add(velocity);
		velocity = velocity.mul(0.91);
	}
}
