// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package world;

import entity.Entity;

import java.util.HashMap;
import java.util.Iterator;

public abstract class World {
	public HashMap<Long, Entity> entities = new HashMap<>();

	public final boolean isClient;

	public World(boolean isClient) {
		this.isClient = isClient;
	}

	public void tick() {
		Iterator<Entity> iterator = entities.values().iterator();
		while(iterator.hasNext()) {
			Entity ent = iterator.next();
			if(ent.dead) {
				onEntityDelete(ent);
				iterator.remove();
				continue;
			}
			ent.tick();
		}
	}

	public void preNetwork() {
		for(Entity entity : entities.values()) {
			entity.preNetwork();
		}
	}

	protected void onEntityDelete(Entity entity) {
	}

	protected void onEntitySpawn(Entity entity) {
	}

	public void spawnEntity(Entity entity) {
		entity.world = this;
		entities.put(entity.id, entity);
		onEntitySpawn(entity);
	}
}
