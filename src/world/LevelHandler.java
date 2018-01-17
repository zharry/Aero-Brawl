package world;

import entity.EntityPlayer;

import java.util.ArrayList;

public abstract class LevelHandler {

	public WorldServer world;
	public Level level;

	// Activators are named: Activator.ID[.RequiredPlayers]
	public void activator(String name, ArrayList<EntityPlayer> playerList) {
		String[] args = name.split("\\.");
		int id = Integer.parseInt(args[1]), colPlayers = args.length <= 2 ? 1 : Integer.parseInt(args[2]);
		if (playerList.size() > 0) {
			if (playerList.size() >= colPlayers) {
				level.setCollidable("Collider." + id, false);
				level.setRenderable("Collider." + id, false);
			} else {
				for (EntityPlayer player: playerList) {
					player.sendMessage(colPlayers - playerList.size() + " more player(s) required!");
				}
			}
		} else {
			level.setCollidable("Collider." + id, true);
			level.setRenderable("Collider." + id, true);
		}
	}
	
	public abstract void onPlayerJoin();
	
	public abstract void onPlayerLeave();
	
	public abstract void collideOther(String object, EntityPlayer player);
	
}
