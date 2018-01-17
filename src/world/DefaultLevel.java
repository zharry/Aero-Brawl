package world;

import java.util.ArrayList;

import entity.EntityPlayer;

public abstract class DefaultLevel extends LevelHandler {

	// Activators are named: Activator.ID[.RequiredPlayers] or Activator.Exit
	public void activator(String name, ArrayList<EntityPlayer> playerList) {
		String[] args = name.split("\\.");

		// Move Player to the next level
		if (name.startsWith("Activator.Exit")) {
			String nextLevel = args[2];
			for (EntityPlayer player : playerList)
				world.setEntityLevel(player, nextLevel);

			// Trigger a activator
		} else {
			int id = Integer.parseInt(args[1]), colPlayers = args.length <= 2 ? 1 : Integer.parseInt(args[2]);
			if (playerList.size() > 0) {
				// Disable corresponding collider
				if (playerList.size() >= colPlayers) {
					level.setCollidable("Collider." + id, false);
					level.setRenderable("Collider." + id, false);
					// Notify players remaining required players
				} else
					for (EntityPlayer player : playerList)
						player.sendMessage(colPlayers - playerList.size() + " more player(s) required!");
				// Enable corresponding collider
			} else {
				level.setCollidable("Collider." + id, true);
				level.setRenderable("Collider." + id, true);
			}
		}
	}

	public void onPlayerJoin(EntityPlayer player) {
		String[] levelData = level.level.split("_");
		String levelID = levelData[0].substring(5);
		String levelDesc = levelData[1];
		player.sendMessage("Level " + levelID + ": " + levelDesc);
	}

	public void collideOther(String object, ArrayList<EntityPlayer> playerList) {
		for (EntityPlayer player : playerList) 
			player.teleportTo("Marker.Spawn");
	}

}
