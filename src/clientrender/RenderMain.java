package clientrender;

import networkclient.ClientStarter;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import packet.CreateGameObject;
import packet.MoveGameObject;
import packet.Packet;

import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

public class RenderMain {

	public ClientStarter client;

	public Random random = new Random();

	public static int frameCounter = 0;
	public float fov = 90;
	public int width;
	public int height;

	public ArrayList<RenderObject> renderObjects = new ArrayList<>();

	public Player player = new Player();

	public HashMap<Long, GameObject> gameObjects = new HashMap<>();

	public double yaw;
	public double pitch;

	public int uSamples;
	public int uTextureFinal;

	public int uDiffuseMap;
	public int uHasDiffuseMap;

	public int aTexCoord;

	public int renderProgram;
	public int postProgram;

	public int finalRenderBuffer;
	public int finalRenderTexture;

	public int samples = 16;

	public double scale = 1;

	public RenderMain(ClientStarter client) {
		this.client = client;
	}

	public void glInit() {

		glClearColor(0.5f, 0.5f, 1.0f, 1);

		renderProgram = GLUtil.loadProgram("/shaders/world.vert", "/shaders/world.frag");
		postProgram = GLUtil.loadProgram("/shaders/post.vert", "/shaders/post.frag");

		glUseProgram(postProgram);
		uSamples = glGetUniformLocation(postProgram, "samples");
		uTextureFinal = glGetUniformLocation(postProgram, "texture");
		glUniform1i(uSamples, samples);

		glActiveTexture(GL_TEXTURE1);
		glUniform1i(uTextureFinal, 1);

		glUseProgram(renderProgram);

		uDiffuseMap = glGetUniformLocation(renderProgram, "diffuseMap");
		uHasDiffuseMap = glGetUniformLocation(renderProgram, "hasDiffuseMap");
		aTexCoord = glGetAttribLocation(renderProgram, "texCoord");

		glActiveTexture(GL_TEXTURE1);
		glUniform1i(uDiffuseMap, 1);


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

		if(Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Mouse.setGrabbed(false);
		}

		yaw += Mouse.getDX();
		pitch += Mouse.getDY();
		if(pitch > 90) {
			pitch = 90;
		}
		if(pitch < -90) {
			pitch = -90;
		}

		double fmove = 0;
		double smove = 0;
		double ymove = 0;

		if(Keyboard.isKeyDown(Keyboard.KEY_W))
			fmove -= 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
			fmove += 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
			smove -= 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			smove += 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			ymove += 1;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			ymove -= 1;

		double yx = Math.cos(Math.toRadians(yaw));
		double yy = Math.sin(Math.toRadians(yaw));

		player.velocity = player.velocity.add(new Vec3(smove * yx - fmove * yy, ymove, smove * yy + fmove * yx).mul(0.001));

		player.position = player.position.add(player.velocity);
		player.velocity = player.velocity.mul(0.91);

		scale *= Math.pow(1.001, Mouse.getDWheel());

		glBindFramebuffer(GL_FRAMEBUFFER, finalRenderBuffer);

		glUseProgram(renderProgram);

		glViewport(0, 0, width, height);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		GLU.gluPerspective(fov, (float) width / height, 0.01f, 1000);

		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();

		glRotated(-pitch, 1, 0, 0);
		glRotated(yaw, 0, 1, 0);
		glTranslated(-player.position.x, -player.position.y, -player.position.z);

		glUniform1i(uHasDiffuseMap, 0);
		glCallList(GLUtil.cubeList);

		glTranslated(0, 1, 0);
		glColor3d(1, 1, 1);
		glScaled(0.04 * scale, 0.04 * scale, 0.04 * scale);

		for(GameObject go : gameObjects.values()) {
			glPushMatrix();
			glTranslated(go.position.x, go.position.y, go.position.z);
			for (RenderObject obj : renderObjects) {
				glUniform1i(uHasDiffuseMap, obj.diffuseTexture);
				glBindTexture(GL_TEXTURE_2D, obj.diffuseTexture);
				if (obj.material != null && obj.material.diffuse != null) {
					glColor3d(obj.material.diffuse.x, obj.material.diffuse.y, obj.material.diffuse.z);
				}
				glCallList(obj.displayList);
			}
			glPopMatrix();
		}

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glUseProgram(postProgram);
		glViewport(0, 0, width, height);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glDisable(GL_LIGHT0);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, finalRenderTexture);

		glBegin(GL_QUADS);
		glTexCoord2d(0, 0);
		glVertex2d(-1, -1);

		glTexCoord2d(1, 0);
		glVertex2d(1, -1);

		glTexCoord2d(1, 1);
		glVertex2d(1, 1);

		glTexCoord2d(0, 1);
		glVertex2d(-1, 1);
		glEnd();
	}

	public void initNetwork() {
		player.id = random.nextLong();
		gameObjects.put(player.id, player);
		client.client.getServerConnection().sendTcp(new CreateGameObject(player.id));
	}

	public void runNetwork() {
		while(!client.packets.isEmpty()) {
			Packet packet = client.packets.poll();
			if(packet instanceof packet.MoveGameObject) {
				MoveGameObject move = (MoveGameObject) packet;
				GameObject object = gameObjects.get(move.id);
				if(object != null) {
					object.position = new Vec3(move.x, move.y, move.z);
					if (move.updateVelocity) {
						object.velocity = new Vec3(move.vx, move.vy, move.vz);
					}
					object.quat = new Quat4(move.qw, move.qx, move.qy, move.qz);
				} else {
					System.out.println("No GameObject with id " + move.id + " not found");
				}
			} else if(packet instanceof packet.CreateGameObject) {
				CreateGameObject create = (CreateGameObject) packet;
				long id = create.id;
				GameObject obj = new GameObject(id);
				gameObjects.put(id, obj);
			}
		}
	}

	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(1280, 720));
			Display.setResizable(true);
			Display.create();
			Display.setVSyncEnabled(true);

			glInit();

			initNetwork();

			long lastUpdate = System.nanoTime();

			while (!Display.isCloseRequested()) {
				++frameCounter;

				long currentTime = System.nanoTime();
				if(currentTime - lastUpdate > 50000000) {
					runNetwork();
					lastUpdate = currentTime;
				}

				if (Display.getWidth() != width || Display.getHeight() != height) {
					width = Display.getWidth();
					height = Display.getHeight();

					if(finalRenderTexture != 0) {
						glDeleteTextures(finalRenderTexture);
					}
					if(finalRenderBuffer != 0) {
						glDeleteRenderbuffers(finalRenderBuffer);
					}


					finalRenderTexture = glGenTextures();
					glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, finalRenderTexture);
//					glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_WRAP_S, GL_REPEAT);
//					glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_WRAP_T, GL_REPEAT);
//					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

					glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGBA8, width, height, false);

					int finalDepthTexture = glGenTextures();
					glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, finalDepthTexture);
					glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_DEPTH_COMPONENT24, width, height, false);

					finalRenderBuffer = glGenFramebuffers();
					glBindFramebuffer(GL_FRAMEBUFFER, finalRenderBuffer);
					glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, finalRenderTexture, 0);
					glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_MULTISAMPLE, finalDepthTexture, 0);

//					int depthBuffer = glGenRenderbuffers();
//					glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
//					glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT32F, width, height);
//					glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);

					int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

					if(status != GL_FRAMEBUFFER_COMPLETE) {
						System.out.println("Framebuffer failed: " + status);
					}

					glViewport(0, 0, width, height);
				}

				glRun();

				Display.update();
			}
			Display.destroy();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public void startRender() {

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
		start();
	}
}
