package world;

import java.util.ArrayList;

import entity.EntityPlayer;

public abstract class LevelHandler {

	public WorldServer world;
	public Level level;

	public abstract void activator(String name, ArrayList<EntityPlayer> playerList);

	public abstract void onPlayerJoin(EntityPlayer player);

	public abstract void collideCollider(String object, ArrayList<EntityPlayer> playerList);

}
