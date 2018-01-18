// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package world;

import entity.Entity;

import java.util.HashMap;
import java.util.Iterator;

// A class to handle the world
public abstract class World {
	// A list of all entities
	public HashMap<Long, Entity> entities = new HashMap<>();

	// Is running as the client?
	public final boolean isClient;

	public World(boolean isClient) {
		this.isClient = isClient;
	}

	// Run a tick
	public void tick() {
		Iterator<Entity> iterator = entities.values().iterator();

		// Delete all dead entities
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

	// Set last tick position for all entities
	public void preNetwork() {
		for(Entity entity : entities.values()) {
			entity.preNetwork();
		}
	}

	// Called when entity gets deleted
	protected void onEntityDelete(Entity entity) {
	}

	// Called when entity spawns
	protected void onEntitySpawn(Entity entity) {
	}

	// Spawn the entity
	public void spawnEntity(Entity entity) {
		entity.world = this;
		entities.put(entity.id, entity);
		onEntitySpawn(entity);
	}
}
