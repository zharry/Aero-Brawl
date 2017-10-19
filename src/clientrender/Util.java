package clientrender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Util {

	public static final Charset charset = Charset.forName("UTF-8");

	public static byte[] readAllBytes(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] b = new byte[4096];
		int read;
		while((read = input.read(b)) != -1) {
			output.write(b, 0, read);
		}
		return output.toByteArray();
	}

	public static String getString(byte[] b) {
		return new String(b, charset);
	}
}
