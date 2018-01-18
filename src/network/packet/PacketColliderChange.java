package network.packet;

public class PacketColliderChange extends Packet {

	private static final long serialVersionUID = -4206140201403181215L;
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
