package util;

import java.util.ArrayList;
import java.util.HashMap;

public class AABBPool {
	public HashMap<String, AABB> aabbs = new HashMap<>();

	public ArrayList<AABB> findAABB(AABB bound) {
		ArrayList<AABB> naabbs = new ArrayList<>();
		for(AABB aabb : aabbs.values()) {
			if(bound.intersect(aabb)) {
				naabbs.add(aabb);
			}
		}
		return naabbs;
	}
}
