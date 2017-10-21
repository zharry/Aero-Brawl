package entity;

import world.World;

public class EntityPlayer extends Entity {

	public String playerName;

	public EntityPlayer() {
		type = EntityRegistry.REGISTRY_EntityPlayer;
	}

	@Override
	public void tick(World world) {
		position = position.add(velocity);
		velocity = velocity.mul(0.91);
	}
}
