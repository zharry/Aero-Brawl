import java.util.ArrayList;

import entity.EntityPlayer;
import world.DefaultLevel;

public class level3_Guess extends DefaultLevel {

	public void activator(String name, ArrayList<EntityPlayer> playerList) {
		String[] args = name.split("\\.");

		// Move Player to the next level
		if (name.startsWith("Activator.Exit")) {
			String nextLevel = args[2];
			for (EntityPlayer player : playerList)
				world.setEntityLevel(player, nextLevel);

			// Trigger activators
		} else {
			for (EntityPlayer player : playerList)
				player.teleportTo("Marker.Spawn");
		}
	}
	
}
