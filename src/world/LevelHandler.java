package world;

import entity.EntityPlayer;

import java.util.ArrayList;

public abstract class LevelHandler {

	public WorldServer world;
	public Level level;

	public abstract void activate(String activator, EntityPlayer player, ArrayList<EntityPlayer> playerList);


}
