package world;

import entity.EntityPlayer;

import java.util.ArrayList;

public abstract class LevelHandler {

	public WorldServer world;
	public Level level;

	public void activator(String activator, ArrayList<EntityPlayer> playerList) {
		
	}

	public void finishLevel(String activator, ArrayList<EntityPlayer> playerList) {
		
	}

	public void collideOther(String object, EntityPlayer player) {
		
	}
	
}
