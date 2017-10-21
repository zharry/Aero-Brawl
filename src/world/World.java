package world;

import entity.Entity;

import java.util.HashMap;

public abstract class World {
	public HashMap<Long, Entity> entities = new HashMap<>();

	public final boolean isClient;

	public World(boolean isClient) {
		this.isClient = isClient;
	}

	public void tick() {
		for(Entity entity : entities.values()) {
			entity.lastPosition = entity.position;
			entity.tick(this);
		}
	}
}
