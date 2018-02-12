// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package world;

// Client side implementation of World
public class WorldClient extends World {

	// Default level
	public Level level = new Level("", this);

	public WorldClient() {
		super(true);
	}
}
