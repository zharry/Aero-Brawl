package world;

import client.ObjLoader;
import entity.EntityPlayer;
import network.packet.PacketColliderChange;
import util.AABB;
import util.Util;
import util.math.Vec3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Level {

	public static File baseDir = new File("obj");

	public String level;
	public byte[] obj;
	public byte[] mtl;

	public HashMap<String, AABB> aabbs = new HashMap<>();

	public HashMap<String, AABB> colliders = new HashMap<>();
	public HashMap<String, AABB> activators = new HashMap<>();

	public HashMap<String, Vec3> locations = new HashMap<>();

	public HashMap<String, String> material = new HashMap<>();

	public ObjLoader loader = new ObjLoader();

	public World world;

	public HashMap<String, HashSet<Long>> playerActivators = new HashMap<>();
	public HashMap<String, HashSet<Long>> playerColliders = new HashMap<>();

	public HashSet<String> dirtyActivators = new HashSet<>();
	public HashSet<String> dirtyColliders = new HashSet<>();

	public Level(String level, World world) {
		this.level = level;
		this.world = world;
	}

	public Vec3 spawnLocation = new Vec3();

	public LevelHandler handler = new LevelHandler() {
		public void activator(String name, ArrayList<EntityPlayer> playerList) { }
		public void onPlayerJoin(EntityPlayer player) { }
		public void collideOther(String object, ArrayList<EntityPlayer> playerList) { }
	};

	public void loadLevelFromFile() throws IOException {
		FileInputStream input = new FileInputStream(new File(new File(baseDir, level), "world.mtl"));
		mtl = Util.readAllBytes(input);
		input.close();

		input = new FileInputStream(new File(new File(baseDir, level), "world.obj"));
		obj = Util.readAllBytes(input);
		input.close();

		loadLevel();

		try {
			handler = (LevelHandler) Class.forName(level).newInstance();
		} catch(Exception e) {
			System.err.println("Cannot load level class!");
			throw new IOException(e);
		}

		if(!world.isClient) {
			handler.world = (WorldServer) world;
			handler.level = this;
		}

	}

	public void runAll() {
		if(!world.isClient) {
			for(String k : dirtyActivators) {
				ArrayList<EntityPlayer> listPlayers = new ArrayList<>();
				for(Long id : playerActivators.get(k)) {
					if(id == null)
						continue;
					listPlayers.add((EntityPlayer) world.entities.get(id));
				}
				try {
					handler.activator(k, listPlayers);
				} catch(Exception e) {
					System.err.println("Custom level code errored: " + level);
					e.printStackTrace();
				}
			}
			for(String k : dirtyColliders) {
				ArrayList<EntityPlayer> listPlayers = new ArrayList<>();
				for(Long id : playerColliders.get(k)) {
					if(id == null)
						continue;
					listPlayers.add((EntityPlayer) world.entities.get(id));
				}
				try {
					handler.collideOther(k, listPlayers);
				} catch(Exception e) {
					System.err.println("Custom level code errored: " + level);
					e.printStackTrace();
				}
			}
		}
	}

	public void clearIteration() {
		dirtyActivators.clear();
		dirtyColliders.clear();
	}

	public void processActivators(String key, long id, boolean intersecting) {
		HashSet<Long> activators = playerActivators.computeIfAbsent(key, k -> new HashSet<>());
		if(activators.contains(id) != intersecting) {
			dirtyActivators.add(key);
			if(intersecting) {
				activators.add(id);
			} else {
				activators.remove(id);
			}
		}
	}

	public void processColliders(String key, long id, boolean intersecting) {
		HashSet<Long> colliders = playerColliders.computeIfAbsent(key, k -> new HashSet<>());
		if(colliders.contains(id) != intersecting) {
			dirtyColliders.add(key);
			if(intersecting) {
				colliders.add(id);
			} else {
				colliders.remove(id);
			}
		}
	}

	public void setCollidable(String object, boolean collidable) {
		AABB aabb = aabbs.get(object);
		aabb.collidable = collidable;
		forceUpdate(object, aabb);
	}

	public void setRenderable(String object, boolean renderable) {
		AABB aabb = aabbs.get(object);
		aabb.renderable = renderable;
		forceUpdate(object, aabb);
	}

	public void setMaterial(String object, String material) {
		AABB aabb = aabbs.get(object);
		aabb.material = material;
		forceUpdate(object, aabb);
	}

	private void forceUpdate(String name, AABB aabb) {
		if(!world.isClient) {
			WorldServer server = (WorldServer) world;
			server.handler.queueBroadcast(level, new PacketColliderChange(name, aabb.collidable, aabb.renderable, aabb.material));
		}
	}

	public void loadLevel() {
		loader.materials = loader.loadMtl(mtl);
		loader.load(obj);
		loadAllAABB();
	}

	public void loadAllAABB() {
		for(ObjLoader.Obj obj : loader.objects) {

			Vec3 min = new Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			Vec3 max = new Vec3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

			for(ObjLoader.Face face : obj.face) {
				for(Vec3 vx : face.vertices) {
					min = Util.min(vx, min);
					max = Util.max(vx, max);
				}
			}

			AABB aabb = new AABB(min, max);
			aabbs.put(obj.name, aabb);

			Vec3 location = aabb.max.add(aabb.min).mul(0.5);
			location = new Vec3(location.x, aabb.max.y + 1e-6, location.z);

			locations.put(obj.name, location);

			split();

		}
	}

	public void split() {
		for(Map.Entry<String, AABB> entry : aabbs.entrySet()) {
			String name = entry.getKey();
			AABB aabb = entry.getValue();
			String[] splits = name.split("\\.");
			String check = splits[0].toLowerCase();

			if (check.equals("marker")) {
				if (splits[1].toLowerCase().startsWith("spawn")) {
					spawnLocation = locations.get(name);
				}
			} else if (check.equals("collider") || check.equals("floor") || check.equals("wall")) {
				colliders.put(name, aabb);
			} else if (check.equals("activator")) {
				activators.put(name, aabb);
			}
		}
	}

}
