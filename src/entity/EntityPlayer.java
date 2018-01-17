// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package entity;

import util.AABB;
import util.math.CollisionFace;
import util.math.Vec3;
import world.WorldClient;
import world.WorldServer;

import java.util.Map;
import java.util.PriorityQueue;

public class EntityPlayer extends Entity {

	public String playerName;

	public final AABB playerAABB = new AABB(new Vec3(-0.25, 0, -0.25), new Vec3(0.25, 1.8, 0.25));
	public final Vec3 eyeOffset = new Vec3(0, 1.6, 0);

	public boolean onGround;
	public boolean prevOnGround;

	public EntityPlayer() {
	}

	@Override
	public void tick() {
		super.tick();
		if (world.isClient) {
			runCollision();
			position = position.add(velocity);
			velocity = velocity.add(new Vec3(0, -0.045, 0));
			if (onGround) {
				velocity = velocity.mul(0.5);
			} else {
				velocity = velocity.mul(0.98);
			}
		} else {
			AABB newAABB = playerAABB.offset(position);
			for (Map.Entry<String, AABB> aabb : ((WorldServer) world).levels.get(level).aabbs.entrySet()) {
				if (aabb.getValue().intersect(newAABB)) {
					String key = aabb.getKey();
//					if (key.startsWith()) {
//						 Do Shit
//					}
				}
			}
		}
	}

	public void teleportTo(String marker) {
		// TODO IMPLEMENT
		throw new RuntimeException("Not implemented");
	}

	public void teleportTo(Vec3 position) {
		// TODO IMPLEMENT
		throw new RuntimeException("Not implemented");
	}

	public void setVelocity(Vec3 velocity) {
		// TODO IMPLEMENT
		throw new RuntimeException("Not implemented");
	}

	public void sendMessage(String message) {
		// TODO IMPLEMENT
		throw new RuntimeException("Not implemented");
	}

	public void runCollision() {

		prevOnGround = onGround;
		onGround = false;

		AABB pos = playerAABB.offset(position);
		CollisionFace[] faces = pos.generateCollisionFaces();

		AABB expanded = pos.expand(velocity).expandAmt(1);

		WorldClient client = (WorldClient) world;
		PriorityQueue<CollisionFace> pq = new PriorityQueue<>();
		System.out.println(client.level.collidables.size());
		boolean[] coll = new boolean[3];
		for (AABB aabb : client.level.collidables.values()) {
			if (aabb.active && aabb.intersect(expanded)) {
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

		while (!pq.isEmpty()) {
			CollisionFace face = pq.remove();

			int faceType = face.type % 3;
			if (coll[faceType]) {
				continue;
			}
			double time = face.collide(faces[(face.type + 3) % 6], velocity);
			if (time == time) {
				coll[faceType] = true;

				double posx = position.x;
				double posy = position.y;
				double posz = position.z;

				double velx = velocity.x;
				double vely = velocity.y;
				double velz = velocity.z;

				switch (face.type) {
					case CollisionFace.NEGX:
						posx = face.coord1 - playerAABB.max.x - 1e-6;
//						if(prevOnGround && face.coord2a2 - face.coord2a1 < stepHeight) {
//							if(!client.level.collides(playerAABB.offset(new Vec3(posx + 2e-6, face.coord2a2 - playerAABB.min.y + 2e-6, position.z)))) {
//								posy = face.coord2a2 - playerAABB.min.y + 1e-6;
//							}
//						}
						velx = 0;
						break;
					case CollisionFace.NEGY:
						posy = face.coord1 - playerAABB.max.y - 1e-6;
						vely = 0;
						break;
					case CollisionFace.NEGZ:
						posz = face.coord1 - playerAABB.max.z - 1e-6;
//						if(prevOnGround && face.coord2b2 - face.coord2b1 < stepHeight) {
//							if(!client.level.collides(playerAABB.offset(new Vec3(position.x, face.coord2b2 - playerAABB.min.y + 2e-6, posz + 2e-6)))) {
//								posy = face.coord2b2 - playerAABB.min.y + 1e-6;
//							}
//						}
						velz = 0;
						break;
					case CollisionFace.POSX:
						posx = face.coord1 - playerAABB.min.x + 1e-6;
//						if(prevOnGround && face.coord2a2 - face.coord2a1 < stepHeight) {
//							if(!client.level.collides(playerAABB.offset(new Vec3(posx - 2e-6, face.coord2a2 - playerAABB.min.y + 2e-6, position.z)))) {
//								posy = face.coord2a2 - playerAABB.min.y + 1e-6;
//							}
//						}
						velx = 0;
						break;
					case CollisionFace.POSY:
						onGround = true;
						posy = face.coord1 - playerAABB.min.y + 1e-6;
						vely = 0;
						break;
					case CollisionFace.POSZ:
						posz = face.coord1 - playerAABB.min.z + 1e-6;
//						if(prevOnGround && face.coord2b2 - face.coord2b1 < stepHeight) {
//							if(!client.level.collides(playerAABB.offset(new Vec3(position.x, face.coord2b2 - playerAABB.min.y + 2e-6, posz - 2e-6)))) {
//								posy = face.coord2b2 - playerAABB.min.y + 1e-6;
//							}
//						}
						velz = 0;
						break;
				}
				position = new Vec3(posx, posy, posz);
				velocity = new Vec3(velx, vely, velz);

				faces = pos.generateCollisionFaces();
			}
		}
	}
}
