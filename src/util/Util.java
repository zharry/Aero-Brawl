// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package util;

import util.math.Quat4;
import util.math.Vec2;
import util.math.Vec3;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

// Some utilities
public class Util {

	public static final Charset charset = Charset.forName("UTF-8");

	public static final DecimalFormat format = new DecimalFormat(" 0.0000;-0.0000");

	public static byte[] readAllBytes(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] b = new byte[4096];
		int read;
		while((read = input.read(b)) != -1) {
			output.write(b, 0, read);
		}
		return output.toByteArray();
	}

	public static byte[] readAllBytesFromFile(File f) throws IOException {
		FileInputStream input = new FileInputStream(f);
		byte[] b = readAllBytes(input);
		input.close();
		return b;
	}

	public static byte[] readAllBytesFromStream(InputStream input) throws IOException {
		byte[] b = readAllBytes(input);
		input.close();
		return b;
	}

	public static byte[] getBytes(String s) {
		return s.getBytes(charset);
	}

	public static String vectorToString(Vec3 vec) {
		return "[" + format.format(vec.x) + ", " + format.format(vec.y) + ", " + format.format(vec.z) + "]";
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

	// Intersect the line defined by l1 + d1t, and l2 + d2t
	public static Vec2 intersectLine(Vec2 l1, Vec2 d1, Vec2 l2, Vec2 d2) {
		double fact = d1.cross(d2);
		Vec2 diff = l1.sub(l2);
		double t1 = d2.cross(diff) / fact;
		double t2 = d1.cross(diff) / fact;
		return new Vec2(t1, t2);
	}

	// Self explanatory
	public static Vec2 intersectRay(Vec2 r1, Vec2 d1, Vec2 l2, Vec2 d2) {
		Vec2 v = intersectLine(r1, d1, l2, d2);
		if(0 <= v.x && 0 <= v.y && v.y <= 1) {
			return new Vec2(v.x, v.y);
		}
		return null;
	}

	// Self explanatory
	public static Vec2 intersectSegment(Vec2 l1, Vec2 d1, Vec2 l2, Vec2 d2) {
		Vec2 v = intersectLine(l1, d1, l2, d2);
		if(0 <= v.x && v.x <= 1 && 0 <= v.y && v.y <= 1) {
			return new Vec2(v.x, v.y);
		}
		return null;
	}

	public static Vec3 max(Vec3 a, Vec3 b) {
		return new Vec3(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
	}

	public static Vec3 min(Vec3 a, Vec3 b) {
		return new Vec3(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
	}
}
