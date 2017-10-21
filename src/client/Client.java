// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package client;

import entity.Entity;
import entity.EntityPlayer;
import entity.EntityRegistry;
import network.client.ClientNetworkHandler;
import network.packet.*;
import org.lwjgl.LWJGLException;
import util.math.Quat4;
import util.math.Vec3;
import world.World;
import world.WorldClient;

import java.net.ConnectException;
import java.util.Random;

public class Client {

	private Random random = new Random();

	public World world;
	public ClientRender render;
	public ClientNetworkHandler network;

	public EntityPlayer player = new EntityPlayer();

	public boolean running;

	public long lastUpdate;
	public double timeDelta;

	public int targetTPS = 30;

	private double nsPerTick = 1e9 / targetTPS;

	public String playerName;

	public Client(String playerName, String ip, int port) throws ConnectException {

		this.playerName = playerName;

		network = new ClientNetworkHandler(ip, port);
		world = new WorldClient();
		render = new ClientRender(this);
	}

	public void run() {
		try {
			render.initRendering(1280, 720);
		} catch(LWJGLException e) {
			e.printStackTrace();
		}

		initNetwork();

		running = true;
		lastUpdate = System.nanoTime();

		while(running) {
			long currentTime = System.nanoTime();
			timeDelta += (currentTime - lastUpdate) / nsPerTick;
			lastUpdate = currentTime;

			while(timeDelta >= 1) {
				timeDelta -= 1;
				world.updatePrevPos();
				runNetwork();
				world.tick();
			}

			render.render(timeDelta);
		}

		System.exit(0);
	}

	public void initNetwork() {
		network.sendPacket(new PacketPlayerJoin(playerName));
	}

	public void runNetwork() {
		while(!network.packets.isEmpty()) {
			Packet packet = network.packets.poll();
			if(packet instanceof PacketMoveEntity) {
				PacketMoveEntity move = (PacketMoveEntity) packet;
				Entity object = world.entities.get(move.id);
				if(object != null) {
					object.position = new Vec3(move.x, move.y, move.z);
					if (move.updateVelocity) {
						object.velocity = new Vec3(move.vx, move.vy, move.vz);
					}
					object.quat = new Quat4(move.qw, move.qx, move.qy, move.qz);
				} else {
					System.out.println("No Entity with id " + move.id);
				}
			} else if(packet instanceof PacketCreateEntity) {
				PacketCreateEntity create = (PacketCreateEntity) packet;

				Entity obj = null;

				System.out.println("Creating: " + create.id + ", " + create.objType + ", " + create.isPlayer);

				switch(create.objType) {
					case EntityRegistry.REGISTRY_EntityPlayer:
						obj = new EntityPlayer();
						if(create.isPlayer) {
							player = (EntityPlayer) obj;
						}
						break;
				}

				if(obj != null) {
					long id = create.id;
					obj.id = id;
					world.entities.put(id, obj);
				} else {
					System.out.println("No Entity registered with type " + create.objType);
				}
			} else if(packet instanceof PacketDeleteEntity) {
				world.entities.remove(((PacketDeleteEntity) packet).id);
			}
		}

		network.sendPacket(new PacketPlayerInput(player.position.x, player.position.y, player.position.z, player.quat.w, player.quat.x, player.quat.y, player.quat.z));
	}
}
