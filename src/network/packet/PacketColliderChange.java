package network.packet;

public class PacketColliderChange extends Packet {

	public String colliderName;

	public boolean collidable;
	public boolean renderable;
	public String material;

	public PacketColliderChange(String colliderName, boolean collidable, boolean renderable, String material) {
		this.colliderName = colliderName;
		this.collidable = collidable;
		this.renderable = renderable;
		this.material = material;
	}
}
