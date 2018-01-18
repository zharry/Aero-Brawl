// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package world;

import entity.EntityPlayer;

import java.util.ArrayList;

public abstract class LevelHandler {

	public WorldServer world;
	public Level level;

	public abstract void activator(String name, ArrayList<EntityPlayer> playerList);

	public abstract void onPlayerJoin(EntityPlayer player);

	public abstract void collideCollider(String object, ArrayList<EntityPlayer> playerList);

}
