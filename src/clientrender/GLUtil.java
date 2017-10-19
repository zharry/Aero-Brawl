package clientrender;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGR;
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


	public static int loadProgram(String vert, String frag) {
		int vertShader = loadShader(vert, GL_VERTEX_SHADER);
		int fragShader = loadShader(frag, GL_FRAGMENT_SHADER);
		if(vertShader == 0 || fragShader == 0) {
			System.out.println("Shaders loading failed");
			return 0;
		}

		int prog = glCreateProgram();
		glAttachShader(prog, vertShader);
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
		int texId = 0;

		try {
			BufferedImage image = ImageIO.read(new File("obj", name));
			DataBufferByte buffer = (DataBufferByte) image.getRaster().getDataBuffer();
			byte[] data = buffer.getData();
			ByteBuffer buf = BufferUtils.createByteBuffer(data.length);
			buf.put(data).flip();
			texId = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texId);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.getWidth(), image.getHeight(), 0, GL_BGR, GL_UNSIGNED_BYTE, buf);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		} catch(Exception e){
			System.out.println("Failed to load texture");
			e.printStackTrace();
		}

		return texId;
	}
}
