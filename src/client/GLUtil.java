// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package client;

import org.lwjgl.BufferUtils;
import util.Util;
import world.Level;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL20.*;

public class GLUtil {

	public static int cubeList;

	public static void init() {
		cubeList = glGenLists(1);
		glNewList(cubeList, GL_COMPILE);

		glBegin(GL_QUADS);

		glColor3d(1, 0, 0);
		glNormal3d(0, 0, 1);
		glVertex3d(-1, -1, 1);
		glVertex3d(1, -1, 1);
		glVertex3d(1, 1, 1);
		glVertex3d(-1, 1, 1);

		glColor3d(0, 1, 0);
		glNormal3d(0, 0, -1);
		glVertex3d(-1, 1, -1);
		glVertex3d(1, 1, -1);
		glVertex3d(1, -1, -1);
		glVertex3d(-1, -1, -1);

		glColor3d(0, 0, 1);
		glNormal3d(0, 1, 0);
		glVertex3d(-1, 1, 1);
		glVertex3d(1, 1, 1);
		glVertex3d(1, 1, -1);
		glVertex3d(-1, 1, -1);

		glColor3d(1, 0, 1);
		glNormal3d(0, -1, 0);
		glVertex3d(-1, -1, -1);
		glVertex3d(1, -1, -1);
		glVertex3d(1, -1, 1);
		glVertex3d(-1, -1, 1);

		glColor3d(1, 1, 0);
		glNormal3d(1, 0, 0);
		glVertex3d(1, -1, -1);
		glVertex3d(1, 1, -1);
		glVertex3d(1, 1, 1);
		glVertex3d(1, -1, 1);

		glColor3d(0, 1, 1);
		glNormal3d(-1, 0, 0);
		glVertex3d(-1, -1, 1);
		glVertex3d(-1, 1, 1);
		glVertex3d(-1, 1, -1);
		glVertex3d(-1, -1, -1);

		glEnd();

		glEndList();
	}

	public static RenderObjectList loadObjToList(Level level, int aTexCoord) {

		ArrayList<RenderObject> renderObjects = new ArrayList<>();

		for(ObjLoader.Obj obj : level.loader.objects) {
			int objList = glGenLists(1);
			int diffuseMap = 0;
			glNewList(objList, GL_COMPILE);

			ObjLoader.Material mat = obj.material;
			if(mat != null) {
				if (mat.diffuse != null) {
					glColor3d(mat.diffuse.x, mat.diffuse.y, mat.diffuse.z);
				}
				if (mat.diffuseMap != null) {
					diffuseMap = GLUtil.loadTexture(mat.diffuseMap);
				}
			}
			for (ObjLoader.Face f : obj.face) {
				glBegin(GL_POLYGON);
				for (int i = 0; i < f.vertices.length; ++i) {
					if(f.textures[i] != null) {
						glVertexAttrib2d(aTexCoord, f.textures[i].x, 1 - f.textures[i].y);
					}
					glNormal3d(f.normals[i].x, f.normals[i].y, f.normals[i].z);
					glVertex3d(f.vertices[i].x, f.vertices[i].y, f.vertices[i].z);
				}
				glEnd();
			}
			glEndList();

			RenderObject robj = new RenderObject();
			robj.diffuseTexture = diffuseMap;
			robj.material = mat;
			robj.displayList = objList;
			renderObjects.add(robj);
		}
		return new RenderObjectList(renderObjects);
	}

	public static void renderObj(RenderObjectList list, int uHasDiffuseMap) {
		if(list == null) {
			return;
		}
		for (RenderObject obj : list.renderObjects) {
			glUniform1i(uHasDiffuseMap, obj.diffuseTexture);
			glBindTexture(GL_TEXTURE_2D, obj.diffuseTexture);
			if (obj.material != null && obj.material.diffuse != null) {
				glColor3d(obj.material.diffuse.x, obj.material.diffuse.y, obj.material.diffuse.z);
			}
			glCallList(obj.displayList);
		}
	}

	public static int loadProgram(String vert, String frag) {
		int vertShader = loadShader(vert, GL_VERTEX_SHADER);
		int fragShader = loadShader(frag, GL_FRAGMENT_SHADER);
		if(vertShader == 0 && vert != null || fragShader == 0 && frag != null) {
			System.out.println("Shaders loading failed");
			return 0;
		}

		int prog = glCreateProgram();
		if(vert != null)
			glAttachShader(prog, vertShader);
		if(frag != null)
			glAttachShader(prog, fragShader);

		glLinkProgram(prog);
		if(glGetProgrami(prog, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Cannot link program");
			System.err.println(glGetProgramInfoLog(prog, glGetProgrami(prog, GL_INFO_LOG_LENGTH)));
			glDeleteProgram(prog);
			return 0;
		}

		glValidateProgram(prog);
		if(glGetProgrami(prog, GL_VALIDATE_STATUS) == GL_FALSE) {
			System.err.println("Cannot validate program");
			System.err.println(glGetProgramInfoLog(prog, glGetProgrami(prog, GL_INFO_LOG_LENGTH)));
			glDeleteProgram(prog);
			return 0;
		}

		return prog;
	}

	public static int loadShader(String name, int type) {
		if(name == null) {
			return 0;
		}
		int shader = glCreateShader(type);
		if(shader == 0) {
			return 0;
		}

		try {
			byte[] bytes = Util.readAllBytes(GLUtil.class.getResourceAsStream(name));
			glShaderSource(shader, (ByteBuffer) BufferUtils.createByteBuffer(bytes.length).put(bytes).flip());
			glCompileShader(shader);
			if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
				System.err.println("Shader loading failed: " + name);
				System.err.println(glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH)));
				glDeleteShader(shader);
				return 0;
			}
			return shader;
		} catch(Exception e) {
			System.err.println("Shader reading failed");
			e.printStackTrace();
		}
		return 0;
	}

	public static int loadTexture(String name) {
		System.out.println(new File("obj", name));
		try (FileInputStream input = new FileInputStream(new File("obj", name))) {
			return loadTexture(input);
		} catch (IOException e) {
			System.err.println("Error occurred opening file");
			e.printStackTrace();
		}
		return 0;
	}

	public static int loadTexture(InputStream stream) {
		try {
			System.out.println("Loading texture");
			BufferedImage image = ImageIO.read(stream);
			stream.close();
			DataBufferByte buffer = (DataBufferByte) image.getRaster().getDataBuffer();
			byte[] data = buffer.getData();
			ByteBuffer buf = BufferUtils.createByteBuffer(data.length);
			for(int i = 0; i < data.length / 4; ++i) {
				buf.put(data[i * 4 + 1]).put(data[i * 4 + 2]).put(data[i * 4 + 3]).put(data[i * 4]);
			}
			buf.flip();
			int texId = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texId);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

			return texId;

		} catch(Exception e){
			System.out.println("Failed to load texture");
			e.printStackTrace();
		}
		return 0;
	}
}
