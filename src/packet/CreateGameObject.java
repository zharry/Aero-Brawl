package packet;

public class CreateGameObject extends Packet {
	public long id;

	public CreateGameObject(long id) {
		this.id = id;
	}
}
