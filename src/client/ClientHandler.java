// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package client;

import entity.Entity;
import entity.EntityPlayer;
import entity.EntityRegistry;
import network.client.ClientNetworkHandler;
import network.packet.*;
import org.lwjgl.LWJGLException;
import util.AABB;
import world.Level;
import world.WorldClient;

import javax.swing.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.TreeMap;

public class ClientHandler {

	public WorldClient world;
	public ClientRender render;
	public ClientNetworkHandler network;

	public boolean spectator = true;
	public EntityPlayer player = new EntityPlayer();

	public boolean running;

	public long lastUpdate;
	public double timeDelta;

	public int targetTPS = 30;

	private double nsPerTick = 1e9 / targetTPS;

	public String playerName;

	public boolean levelDirty;

	public TreeMap<Long, String> messageQueue = new TreeMap<Long, String>();
	private final int MESSAGEALIVE = 5000;

	public ClientHandler(String playerName, String ip, int port) throws IOException {

		this.playerName = playerName;

		network = new ClientNetworkHandler(ip, port);
		world = new WorldClient();
		render = new ClientRender(this);
	}

	public void run() {
		try {
			render.initRendering(1280, 720);
		} catch (LWJGLException e) {
			JOptionPane.showMessageDialog(null,
					"OpenGL cannot be initialized on your computer\n" + e.toString()
							+ "\nMake sure you install the latest graphics drivers",
					"OpenGL error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}

		initNetwork();

		running = true;
		lastUpdate = System.nanoTime();

		while (running) {
			long currentTime = System.nanoTime();
			timeDelta += (currentTime - lastUpdate) / nsPerTick;
			lastUpdate = currentTime;

			while (timeDelta >= 1) {
				timeDelta -= 1;
				world.preNetwork();
				runNetwork();
				world.tick();
				render.runInput();
				checkMessageQueue();
			}

			render.render(timeDelta);
		}

		System.exit(0);
	}

	public void initNetwork() {
		network.sendPacket(new PacketPlayerJoin(playerName));
	}

	public void checkMessageQueue() {
		while (!messageQueue.isEmpty() && System.currentTimeMillis() - MESSAGEALIVE > messageQueue.firstKey()) {
			messageQueue.remove(messageQueue.firstKey());
		}
	}

	public void runNetwork() {
		while (!network.packets.isEmpty()) {
			Packet packet = network.packets.poll();
			try {
				if (packet instanceof PacketEntityUpdate) {
					PacketEntityUpdate update = (PacketEntityUpdate) packet;
					ByteBuffer buffer = ByteBuffer.wrap(update.entityUpdateData);
					Entity entity = world.entities.get(update.id);
					if (entity != player || update.force) {
						entity.monitor.deserialize(buffer);
					}
				} else if (packet instanceof PacketEntitySpawn) {
					PacketEntitySpawn spawn = (PacketEntitySpawn) packet;
					Entity entity = EntityRegistry.idToClass.get(spawn.entityClassId).newInstance();
					entity.id = spawn.id;
					world.spawnEntity(entity);
				} else if (packet instanceof PacketEntityDelete) {
					PacketEntityDelete delete = (PacketEntityDelete) packet;
					world.entities.get(delete.id).dead = true;
				} else if (packet instanceof PacketEntitySetPlayer) {
					PacketEntitySetPlayer setPlayer = (PacketEntitySetPlayer) packet;
					System.out.println("Change player: " + setPlayer.id);
					player = (EntityPlayer) world.entities.get(setPlayer.id);
					spectator = setPlayer.spectator;
				} else if (packet instanceof PacketColliderChange) {
					PacketColliderChange change = (PacketColliderChange) packet;
					AABB aabb = world.level.aabbs.get(change.colliderName);
					aabb.collidable = change.collidable;
					aabb.renderable = change.renderable;
					aabb.material = change.material;
				} else if (packet instanceof PacketNewWorld) {
					PacketNewWorld newWorld = (PacketNewWorld) packet;
					System.out.println("Loading level: " + newWorld.level);

					world.level = new Level(newWorld.level, world);
					world.level.mtl = newWorld.mtl;
					world.level.obj = newWorld.obj;
					world.level.loadLevel();
					world.level.aabbs = newWorld.aabbs;
					world.level.split();

					world.entities.clear();
					levelDirty = true;
				} else if (packet instanceof PacketMessage) {
					PacketMessage message = (PacketMessage) packet;
					String mesg = message.message;
					messageQueue.put(System.currentTimeMillis(), mesg);
				}
			} catch (Exception e) {
				System.err.println("Exception occurred while processing packet: " + packet);
				e.printStackTrace();
			}
		}

		network.sendPacket(new PacketPlayerInput(player.position.x, player.position.y, player.position.z, player.quat.w,
				player.quat.x, player.quat.y, player.quat.z));
	}
}
