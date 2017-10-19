package clientrender;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

public class RenderMain {

	public static int frameCounter = 0;
	public float fov = 90;
	public int width;
	public int height;

	public ArrayList<RenderObject> renderObjects = new ArrayList<>();

	public int uDiffuseMap;
	public int uHasDiffuseMap;

	public int aTexCoord;

	public int renderProgram;

	public double scale = 1;

	public RenderMain() {
	}

	public void glInit() {

		renderProgram = GLUtil.loadProgram("/shaders/world.vert", "/shaders/world.frag");

		glUseProgram(renderProgram);

		uDiffuseMap = glGetUniformLocation(renderProgram, "diffuseMap");
		uHasDiffuseMap = glGetUniformLocation(renderProgram, "uHasDiffuseMap");
		aTexCoord = glGetAttribLocation(renderProgram, "texCoord");

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glEnable(GL_COLOR_MATERIAL);

		glLight(GL_LIGHT0, GL_DIFFUSE, (FloatBuffer) BufferUtils.createFloatBuffer(4).put(1).put(1).put(1).put(3).flip());
		glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) BufferUtils.createFloatBuffer(4).put(1).put(0).put(0).put(1).flip());


		GLUtil.init();

		try {
			ObjLoader loader = new ObjLoader();
			FileInputStream input = new FileInputStream("obj/sinon-sword-art-online.obj");
			loader.load(Util.readAllBytes(input));
			input.close();

			for(ObjLoader.Obj obj : loader.objects) {
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
						glActiveTexture(GL_TEXTURE1);

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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void glRun() {

		scale *= Math.pow(1.001, Mouse.getDWheel());

		glUseProgram(renderProgram);

		glViewport(0, 0, width, height);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, (float) width / height, 0.01f, 1000);
		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();

		glTranslated(0, 0, -5);
		glRotated(-Mouse.getY(), 1, 0, 0);
		glRotated(Mouse.getX(), 0, 1, 0);
		glTranslated(0, -2, 0);
		glCallList(GLUtil.cubeList);

		glTranslated(0, 1, 0);
		glColor3d(1, 1, 1);
		glScaled(0.04 * scale, 0.04 * scale, 0.04 * scale);

		for(RenderObject obj : renderObjects) {
			glUniform1i(uHasDiffuseMap, obj.diffuseTexture);
			glUniform1i(uDiffuseMap, obj.diffuseTexture);
			if(obj.material != null && obj.material.diffuse != null) {
				glColor3d(obj.material.diffuse.x, obj.material.diffuse.y, obj.material.diffuse.z);
			}
			glCallList(obj.displayList);
		}
	}

	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(1280, 720));
			Display.setResizable(true);
			Display.create();
			Display.setVSyncEnabled(true);

			glInit();

			while (!Display.isCloseRequested()) {
				++frameCounter;

				if (Display.getWidth() != width || Display.getHeight() != height) {
					width = Display.getWidth();
					height = Display.getHeight();
				}

				glRun();

				Display.update();
			}
			Display.destroy();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Thread() {
			{
				setDaemon(true);
			}

			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					System.out.println(frameCounter);
					frameCounter = 0;
				}
			}
		}.start();
		new RenderMain().start();
	}
}
