// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package util;

import util.math.Quat4;
import util.math.Vec3;

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

	public static byte[] getBytes(String s) {
		return s.getBytes(charset);
	}

	public static String getString(byte[] b) {
		return new String(b, charset);
	}

	public static Vec3 mix(Vec3 a, Vec3 b, double factor) {
		return b.sub(a).mul(factor).add(a);
	}

	public static Quat4 mixLinear(Quat4 a, Quat4 b, double factor) {
		return b.sub(a).mul(factor).add(a);
	}
}
