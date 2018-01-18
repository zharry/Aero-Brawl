// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

import entity.EntityPlayer;
import world.DefaultLevel;

import java.util.ArrayList;

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
