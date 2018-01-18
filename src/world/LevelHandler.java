// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package world;

import entity.EntityPlayer;

import java.util.ArrayList;

public abstract class LevelHandler {

	// World and level
	public WorldServer world;
	public Level level;

	// When player stands on activator
	public abstract void activator(String name, ArrayList<EntityPlayer> playerList);

	// When player joins
	public abstract void onPlayerJoin(EntityPlayer player);

	// When player is inside a collider
	public abstract void collideCollider(String object, ArrayList<EntityPlayer> playerList);

}
