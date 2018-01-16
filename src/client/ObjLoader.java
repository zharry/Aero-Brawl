// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package client;

import util.math.Vec2;
import util.math.Vec3;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ObjLoader {

	public ArrayList<Obj> objects = new ArrayList<>();
	public HashMap<String, Material> materials = new HashMap<>();

	public void load(byte[] data) {
		ArrayList<Vec3> vertexCoord = new ArrayList<>();
		ArrayList<Vec2> textureCoord = new ArrayList<>();
		ArrayList<Vec3> normals = new ArrayList<>();

		String objName = "";
		ArrayList<Face> faces = new ArrayList<>();

		Material usedMaterial = null;

		StringTokenizer tokenizer = new StringTokenizer(new String(data, Charset.forName("ASCII")), "\r\n", false);
		while(tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken().trim();
			if(token.length() == 0 || token.startsWith("#")) {
				continue;
			}
			String[] tok = token.split(" ");
			switch (tok[0]) {
				case "v": {
					Vec3 vec = new Vec3(Double.parseDouble(tok[1]), Double.parseDouble(tok[2]), Double.parseDouble(tok[3]));
					if (tok.length == 5) {
						vec = vec.mul(1 / Double.parseDouble(tok[4]));
					}
					vertexCoord.add(vec);
					break;
				}
				case "vt": {
					Vec2 vec = new Vec2(Double.parseDouble(tok[1]), Double.parseDouble(tok[2]));
					textureCoord.add(vec);
					break;
				}
				case "vn":
					Vec3 normal = new Vec3(Double.parseDouble(tok[1]), Double.parseDouble(tok[2]), Double.parseDouble(tok[3]));
					normals.add(normal);
					break;
				case "f":
					Face face = new Face(tok.length - 1);
					for (int i = 0; i < tok.length - 1; ++i) {
						String[] str = tok[i + 1].split("/");
						face.vertices[i] = vertexCoord.get(Integer.parseInt(str[0]) - 1);
						if (str.length > 1 && str[1].trim().length() != 0) {
							face.textures[i] = textureCoord.get(Integer.parseInt(str[1]) - 1);
						}
						if (str.length > 2 && str[2].trim().length() != 0) {
							face.normals[i] = normals.get(Integer.parseInt(str[2]) - 1);
						}
					}
					faces.add(face);
					break;
				case "o":
					if(faces.size() > 0) {
						objects.add(new Obj(objName, faces, usedMaterial));
					}
					if(tok.length > 1) {
						objName = tok[1];
					} else {
						objName = "";
					}
					faces = new ArrayList<>();
					break;
//				case "mtllib":
//					if(tok.length > 1) {
//						try {
//							FileInputStream input = new FileInputStream(new File("obj", tok[1]));
//							HashMap<String, Material> mats = loadMtl(Util.readAllBytes(input));
//							input.close();
//							materials.putAll(mats);
//						} catch(IOException e) {
//							System.err.println("Unable to load mtl file: " + tok[1]);
//							e.printStackTrace();
//						}
//					}
//					break;
				case "usemtl":
					if(tok.length > 1) {
						usedMaterial = materials.get(tok[1]);
					}
					break;
			}
		}
		if(faces.size() > 0) {
			objects.add(new Obj(objName, faces, usedMaterial));
		}
	}

	public HashMap<String, Material> loadMtl(byte[] data) {
		StringTokenizer tokenizer = new StringTokenizer(new String(data, Charset.forName("ASCII")), "\r\n", false);
		String matName = "";

		HashMap<String, Material> materials = new HashMap<>();

		Material material = new Material();

		while(tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken().trim();
			if(token.length() == 0 || token.startsWith("#")) {
				continue;
			}
			String[] tok = token.split(" ");
			switch (tok[0]) {
				case "newmtl":
					if(tok.length != 2) {
						System.err.println("No material name specified");
					} else {
						materials.put(matName, material);
						material = new Material();
						matName = tok[1];
						material.name = matName;
					}
					break;
				case "Kd":
					if(tok.length == 4) {
						material.diffuse = new Vec3(Double.parseDouble(tok[1]), Double.parseDouble(tok[2]), Double.parseDouble(tok[3]));
					}
					break;
				case "map_Kd":
					if(tok.length == 2) {
						material.diffuseMap = tok[1];
					}
					break;
			}
		}

		return materials;
	}

	public static class Face {
		public Vec3[] vertices;
		public Vec2[] textures;
		public Vec3[] normals;
		public Face(int n) {
			vertices = new Vec3[n];
			textures = new Vec2[n];
			normals = new Vec3[n];
		}
	}

	public static class Material{

		public String name;

		public Vec3 ambient;
		public Vec3 diffuse;
		public Vec3 specular;
		public double specExp;
		public int illum;

		public String ambientMap;
		public String diffuseMap;
	}

	public static class Obj {
		public String name;
		public ArrayList<Face> face;
		public Material material;
		public Obj(String name, ArrayList<Face> face, Material material) {
			this.name = name;
			this.face = face;
			this.material = material;
		}
	}

}
