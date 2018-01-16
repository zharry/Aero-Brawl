// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package world;

import entity.Entity;
import entity.EntityRegistry;
import network.packet.PacketColliderChange;
import network.packet.PacketEntityDelete;
import network.packet.PacketEntitySpawn;
import network.packet.PacketEntityUpdate;
import network.server.ServerHandler;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class WorldServer extends World {

	private ByteBuffer buffer = ByteBuffer.allocate(65536);

	private ServerHandler handler;

	public HashMap<String, Level> levels = new HashMap<>();

	public WorldServer(ServerHandler handler) {
		super(false);
		this.handler = handler;
	}

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
	}

	public void forceUpdate(Entity entity) {
		buffer.clear();
		entity.monitor.serialize(buffer, true);
		int length = buffer.position();
		byte[] bytes = new byte[length];
		buffer.flip();
		buffer.get(bytes);
		handler.queueBroadcast(entity.level, new PacketEntityUpdate(entity.id, true, bytes));
	}

	public void setEntityLevel(Entity entity, String level) {
		handler.queueBroadcast(entity.level, new PacketEntityDelete(entity.id));
		entity.level = level;
		handler.queueBroadcast(entity.level, new PacketEntitySpawn(entity.id, EntityRegistry.classToId.get(entity.getClass())));
	}

	public void setColliderEnable(String collider, String level, boolean state) {
		levels.get(level).aabbs.get(collider).active = state;
		handler.queueBroadcast(level, new PacketColliderChange(collider, state));
	}

	protected void onEntityDelete(Entity entity) {
		handler.queueBroadcast(entity.level, new PacketEntityDelete(entity.id));
	}

	protected void onEntitySpawn(Entity entity) {
		handler.queueBroadcast(entity.level, new PacketEntitySpawn(entity.id, EntityRegistry.classToId.get(entity.getClass())));
	}

}
