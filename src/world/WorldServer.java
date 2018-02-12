// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package world;

import entity.Entity;
import entity.EntityPlayer;
import entity.EntityRegistry;
import network.packet.PacketEntityDelete;
import network.packet.PacketEntitySpawn;
import network.packet.PacketEntityUpdate;
import network.packet.PacketMessage;
import network.server.ServerHandler;

import java.nio.ByteBuffer;
import java.util.HashMap;

// Server side implementation of World
public class WorldServer extends World {

	private ByteBuffer buffer = ByteBuffer.allocate(65536);

	// Handler to handle network
	public ServerHandler handler;

	// List of levels
	public HashMap<String, Level> levels = new HashMap<>();

	public WorldServer(ServerHandler handler) {
		super(false);
		this.handler = handler;
	}

	// When ticking, update all the entity, send to everyone
	public void tick() {
		super.tick();
		for(Entity entity : entities.values()) {
			buffer.clear();
			entity.monitor.serialize(buffer, false);
			int length = buffer.position();
			byte[] bytes = new byte[length];
			buffer.flip();
			buffer.get(bytes);
			handler.queueBroadcast(entity.level, new PacketEntityUpdate(entity.id, false, bytes));
		}
		for(Level level : levels.values()) {
			level.runAll();
		}
	}

	// Force update entity property to everyone
	public void forceUpdate(Entity entity) {
		buffer.clear();
		entity.monitor.serialize(buffer, true);
		int length = buffer.position();
		byte[] bytes = new byte[length];
		buffer.flip();
		buffer.get(bytes);
		handler.queueBroadcast(entity.level, new PacketEntityUpdate(entity.id, true, bytes));
	}

	// Broadcast the message to everyone
	public void broadcastMessage(String message, String level) {
		handler.queueBroadcast(level, new PacketMessage(message));
	}

	// Change the level of an entity
	public void setEntityLevel(Entity entity, String level) {
		if(!levels.containsKey(level)) {
			throw new RuntimeException("No such level: " + level);
		}
		handler.queueBroadcast(entity.level, new PacketEntityDelete(entity.id));
		handler.setPlayerLevel(level, entity.level, entity.id);
		levels.get(entity.level).flushPlayer(entity.id);
//		handler.queueBroadcast(entity.level, new PacketEntitySpawn(entity.id, EntityRegistry.classToId.get(entity.getClass())));
	}

	// Process entity deletion
	protected void onEntityDelete(Entity entity) {
		levels.get(entity.level).flushPlayer(entity.id);
		handler.queueBroadcast(entity.level, new PacketEntityDelete(entity.id));
	}

	// Process entity spawn
	protected void onEntitySpawn(Entity entity) {
		if(entity instanceof EntityPlayer) {
			try {
				// Call onPlayerJoin on the LevelHandler
				levels.get(entity.level).handler.onPlayerJoin((EntityPlayer) entity);
			} catch(Exception e) {
				System.err.println("Custom level code errored: " + entity.level);
				e.printStackTrace();
			}
		}
		handler.queueBroadcast(entity.level, new PacketEntitySpawn(entity.id, EntityRegistry.classToId.get(entity.getClass())));
	}

}
