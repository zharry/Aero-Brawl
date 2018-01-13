// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package world;

import entity.Entity;
import entity.EntityRegistry;
import network.packet.PacketEntityDelete;
import network.packet.PacketEntitySpawn;
import network.packet.PacketEntityUpdate;
import network.server.ServerHandler;

import java.nio.ByteBuffer;

public class WorldServer extends World {

	private ByteBuffer buffer = ByteBuffer.allocate(65536);

	private ServerHandler handler;

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
			handler.queueBroadcast(new PacketEntityUpdate(entity.id, false, bytes));
		}
	}

	protected void onEntityDelete(Entity entity) {
		handler.queueBroadcast(new PacketEntityDelete(entity.id));
	}

	protected void onEntitySpawn(Entity entity) {
		handler.queueBroadcast(new PacketEntitySpawn(entity.id, EntityRegistry.classToId.get(entity.getClass())));
	}

}
