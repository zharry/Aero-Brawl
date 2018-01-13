// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package entity;

import java.util.HashMap;

public class EntityRegistry {

	public static final HashMap<Class<? extends Entity>, Integer> classToId = new HashMap<>();
	public static final HashMap<Integer, Class<? extends Entity>> idToClass = new HashMap<>();

	private static void registerEntity(Class<? extends Entity> clazz, int id) {
		classToId.put(clazz, id);
		idToClass.put(id, clazz);
	}

	static {
		registerEntity(EntityPlayer.class, 1);
	}
}
