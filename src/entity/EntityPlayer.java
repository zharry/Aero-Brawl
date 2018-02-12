// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package entity;

import network.packet.Packet;
import network.packet.PacketMessage;
import util.AABB;
import util.math.CollisionFace;
import util.math.Vec3;
import world.Level;
import world.WorldClient;
import world.WorldServer;

import java.util.Map;
import java.util.PriorityQueue;

// The player entity
public class EntityPlayer extends Entity {

	public String playerName;

	// Bounding box of the player
	public final AABB playerAABB = new AABB(new Vec3(-0.325, 0, -0.325), new Vec3(0.325, 1.8, 0.325));
	public final Vec3 eyeOffset = new Vec3(0, 1.6, 0);

	public boolean onGround;
	public boolean prevOnGround;


	// Synchronize this across all clients
	@Synchronize
	// Is spectating?
	public boolean spectate;

	public EntityPlayer() {
	}

	@Override
	public void tick() {
		super.tick();
		if (world.isClient) {
			// If we're on the client side, run the collision
			if(!spectate) {
				runCollision();
			} else {
				onGround = false;
			}

			// Accelerate and move the player
			position = position.add(velocity);
			if(!spectate) {
				velocity = velocity.add(new Vec3(0, -0.045, 0));
			}
			if (onGround) {
				velocity = velocity.mul(0.5);
			} else {
				velocity = velocity.mul(0.98);
			}
		} else {
			// If we're on the server side, run the bounds checking
			if(spectate)
				return;
			WorldServer server = (WorldServer) world;
			Level level = server.levels.get(this.level);
			AABB newAABB = playerAABB.offset(position);
			// Check to see if the player is inside an activator or an active collider
			for (Map.Entry<String, AABB> aabb : level.activators.entrySet()) {
				level.processActivators(aabb.getKey(), id, aabb.getValue().intersect(newAABB));
			}
			for (Map.Entry<String, AABB> aabb : level.colliders.entrySet()) {
				level.processColliders(aabb.getKey(), id, aabb.getValue().collidable && aabb.getValue().intersect(newAABB));
			}
		}
	}

	// Teleport player to marker
	public void teleportTo(String marker) {
		if(!world.isClient) {
			WorldServer server = (WorldServer) world;
			Level currLevel = server.levels.get(level);
			Vec3 pos = currLevel.locations.get(marker);
			if(pos == null)
				throw new RuntimeException("No such marker: " + marker);
			teleportTo(pos);
		}
	}

	// Teleport to a position
	public void teleportTo(Vec3 position) {
		this.position = position;
		forceUpdate();
	}

	// Change the player velocity
	public void setVelocity(Vec3 velocity) {
		this.velocity = velocity;
		forceUpdate();
	}

	// Force an player state update to everyone
	private void forceUpdate() {
		if(!world.isClient) {
			WorldServer server = (WorldServer) world;
			server.forceUpdate(this);
		}
	}

	// Send a message to this player
	public void sendMessage(String message) {
		sendPacket(new PacketMessage(message));
	}

	// Send a packet to this player
	private void sendPacket(Packet packet) {
		if(!world.isClient) {
			WorldServer server = (WorldServer) world;
			server.handler.queuePacket(id, packet);
		}
	}

	// Run the collision and response
	public void runCollision() {

		prevOnGround = onGround;
		onGround = false;

		AABB pos = playerAABB.offset(position);
		CollisionFace[] faces = pos.generateCollisionFaces();

		AABB expanded = pos.expand(velocity).expandAmt(1);

		WorldClient client = (WorldClient) world;
		PriorityQueue<CollisionFace> pq = new PriorityQueue<>();
		boolean[] coll = new boolean[3];

		// Split each AABB into 6 faces.
		// Then for at most 3 of the faces, add it to the list of faces that could be collided with
		// This list will be sorted, so that the closest face gets collided and responded first
		for (AABB aabb : client.level.colliders.values()) {
			if (aabb.collidable && aabb.intersect(expanded)) {
				for (CollisionFace face : aabb.generateCollisionFaces()) {
					double vv = 0;
					switch (face.type % 3) {
						case CollisionFace.NEGX:
							vv = velocity.x;
							break;
						case CollisionFace.NEGY:
							vv = velocity.y;
							break;
						case CollisionFace.NEGZ:
							vv = velocity.z;
							break;
					}
					if ((face.type >= 3) == (vv >= 0)) {
						continue;
					}

					face.dist = Math.abs(face.coord1 - faces[(face.type + 3) % 6].coord1);

					pq.add(face);
				}
			}
		}

		// Go through all the faces now
		while (!pq.isEmpty()) {
			CollisionFace face = pq.remove();

			int faceType = face.type % 3;
			if (coll[faceType]) {
				continue;
			}

			// Try colliding the face, considering the player's velocity
			double time = face.collide(faces[(face.type + 3) % 6], velocity);
			// Collides
			if (time == time) {
				coll[faceType] = true;

				double posx = position.x;
				double posy = position.y;
				double posz = position.z;

				double velx = velocity.x;
				double vely = velocity.y;
				double velz = velocity.z;

				// Check to see which face it was, and respond accordingly
				switch (face.type) {
					case CollisionFace.NEGX:
						posx = face.coord1 - playerAABB.max.x - 1e-6;
						velx = 0;
						break;
					case CollisionFace.NEGY:
						posy = face.coord1 - playerAABB.max.y - 1e-6;
						vely = 0;
						break;
					case CollisionFace.NEGZ:
						posz = face.coord1 - playerAABB.max.z - 1e-6;
						velz = 0;
						break;
					case CollisionFace.POSX:
						posx = face.coord1 - playerAABB.min.x + 1e-6;
						velx = 0;
						break;
					case CollisionFace.POSY:
						onGround = true;
						posy = face.coord1 - playerAABB.min.y + 1e-6;
						vely = 0;
						break;
					case CollisionFace.POSZ:
						posz = face.coord1 - playerAABB.min.z + 1e-6;
						velz = 0;
						break;
				}

				// Change the player's position and velocity in order to respond to this collision
				position = new Vec3(posx, posy, posz);
				velocity = new Vec3(velx, vely, velz);

				// Regenerate the collision faces at the new position
				faces = pos.generateCollisionFaces();
			}
		}
	}
}
