package clientrender;

public abstract class GameObject {

	public Vec3 position = new Vec3();
	public Vec3 velocity = new Vec3();

	public Quat4 quat = new Quat4();

	public RenderObject render;

}
