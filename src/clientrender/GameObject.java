package clientrender;

public class GameObject {

	public long id;

	public Vec3 position = new Vec3();
	public Vec3 velocity = new Vec3();

	public Quat4 quat = new Quat4();

	public RenderObject render;

	public GameObject() {
	}

	public GameObject(long id) {
		this.id = id;
	}

}
