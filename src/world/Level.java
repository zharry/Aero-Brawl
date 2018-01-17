package world;

import client.ObjLoader;
import util.AABB;
import util.Util;
import util.math.Vec3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class Level {

	public static File baseDir = new File("obj");

	public String level;
	public byte[] obj;
	public byte[] mtl;

	public HashMap<String, AABB> aabbs = new HashMap<>();

	public HashMap<String, AABB> collidables = new HashMap<>();

	public HashMap<String, Vec3> locations = new HashMap<>();

	public HashMap<String, Boolean> renderable = new HashMap<>();
	public HashMap<String, Boolean> collidable = new HashMap<>();
	public HashMap<String, String> material = new HashMap<>();

	public ObjLoader loader = new ObjLoader();

	public Level(String level) {
		this.level = level;
	}

	public Vec3 spawnLocation = new Vec3();

	public LevelHandler handler;

	public World world;

	public Level(World world) {
		this.world = world;
	}

	public void loadLevelFromFile() throws IOException {
		FileInputStream input = new FileInputStream(new File(new File(baseDir, level), "world.mtl"));
		mtl = Util.readAllBytes(input);
		input.close();

		input = new FileInputStream(new File(new File(baseDir, level), "world.obj"));
		obj = Util.readAllBytes(input);
		input.close();

		loadLevel();

		// TODO load level handler class

	}

	public boolean collides(AABB other) {
		for(AABB aabb : collidables.values()) {
			if(aabb.intersect(other)) {
				return true;
			}
		}
		return false;
	}

	public void setRenderable(String object, boolean enabled) {
		// TODO IMPLEMENT
		throw new RuntimeException("Not implemented");
	}

	public void setCollidable(String object, boolean collidable) {
		// TODO IMPLEMENT
		throw new RuntimeException("Not implemented");
	}

	public void setMaterial(String object, String material) {
		// TODO IMPLEMENT
		throw new RuntimeException("Not implemented");
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
			String[] splits = obj.name.split("\\.");
			String check = splits[0].toLowerCase();

			Vec3 location = aabb.max.add(aabb.min).mul(0.5);
			location = new Vec3(location.x, aabb.max.y + 1e-6, location.z);

			locations.put(obj.name, location);

			if(check.equals("marker")) {
				if(splits[1].toLowerCase().startsWith("spawn")) {
					spawnLocation = location;
				}
			}
//			if(check.equals("collider") || check.equals("floor") || check.equals("wall") || check.equals("cube")) {
				collidables.put(obj.name, aabb);
//			}
		}
	}

}
