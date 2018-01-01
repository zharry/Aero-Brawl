import java.lang.reflect.Field;

public class ReflectionTest {
	public int field = 3;
	public ReflectionTest() {
		try {
			Field field = getClass().getDeclaredField("field");
			long start = System.nanoTime();
			for(long i = 0; i < 1000000000L; ++i) {
				this.field = (int)i;
			}
			System.out.println("Native setting: " + (System.nanoTime() - start) / 1e6);
			start = System.nanoTime();
			for(long i = 0; i < 1000000000L; ++i) {
				field = getClass().getDeclaredField("field");
				field.set(this, (int)i);
			}
			System.out.println("Reflection with field getting: " + (System.nanoTime() - start) / 1e6);
			start = System.nanoTime();
			for(long i = 0; i < 1000000000L; ++i) {
				field.set(this, (int)i);
			}
			System.out.println("Reflection: " + (System.nanoTime() - start) / 1e6);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new ReflectionTest();
	}
}
