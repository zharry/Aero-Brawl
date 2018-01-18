// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package client;

import entity.Entity;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import util.AABB;
import util.Util;
import util.math.Quat4;
import util.math.Vec3;
import world.Level;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

public class ClientRender {

	public ClientHandler client;

	public int frameCounter = 0;
	public int fps = 0;
	public float fov = 90;
	public int width;
	public int height;

	public int uSamples;
	public int uTextureFinal;

	public int uShadowMap;
	public int uDiffuseMap;
	public int uHasDiffuseMap;
	public int uShadowMapSize;
	public int uView;

	public int uViewShadow;

	public FloatBuffer shadowProjection = BufferUtils.createFloatBuffer(16);
	public FloatBuffer shadowView = BufferUtils.createFloatBuffer(16);
	public FloatBuffer view = BufferUtils.createFloatBuffer(16);

	public Vec3[] cubemapDirs = { new Vec3(1.0, 0.0, 0.0), new Vec3(0.0, -1.0, 0.0), new Vec3(-1.0, 0.0, 0.0),
			new Vec3(0.0, -1.0, 0.0), new Vec3(0.0, 1.0, 0.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, -1.0, 0.0),
			new Vec3(0.0, 0.0, -1.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, -1.0, 0.0), new Vec3(0.0, 0.0, -1.0),
			new Vec3(0.0, -1.0, 0.0), };

	public Vec3 lightPosition = new Vec3();

	public int aTexCoord;

	public int renderProgram;
	public int postProgram;
	public int shadowProgram;

	public int finalRenderBuffer;
	public int finalRenderTexture;

	public int shadowRenderBuffer;
	public int shadowRenderTexture;

	public int shadowMapSize = 1024;

	public RenderObjectList worldModel, playerModel;

	public double rotX;
	public double rotY;

	public int samples = 4;

	public double scale = 1;

	public static boolean isCaptured;

	public static boolean isDebugOpen;

	public ContextCapabilities capabilities;

	public ClientRender(ClientHandler client) {
		this.client = client;
	}

	public void initRendering(int width, int height) throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setResizable(true);
		Display.create();
		Display.setVSyncEnabled(true);

		System.out.println(glGetString(GL_VERSION));

		capabilities = GLContext.getCapabilities();

		if (!capabilities.OpenGL30) {
			throw new LWJGLException("OpenGL 3.0 is not supported.");
		}

		try {
			Field field = Field.class.getDeclaredField("modifiers");
			field.setAccessible(true);
			Field cap = capabilities.getClass().getDeclaredField("OpenGL32");
			field.set(cap, Modifier.PUBLIC);
			cap.set(capabilities, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		glInit();

		new Thread(() -> {
			while (true) {
				fps = frameCounter;
				frameCounter = 0;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public void glInit() {

		glClearColor(0.5f, 0.5f, 1.0f, 1);

		renderProgram = GLUtil.loadProgram("/shaders/world.vert", "/shaders/world.frag");
		postProgram = GLUtil.loadProgram("/shaders/post.vert", "/shaders/post.frag");
		shadowProgram = GLUtil.loadProgram("/shaders/shadow.vert", "/shaders/shadow.frag");

		glUseProgram(postProgram);
		uSamples = glGetUniformLocation(postProgram, "samples");
		uTextureFinal = glGetUniformLocation(postProgram, "texture");
		glUniform1i(uSamples, samples);

		glActiveTexture(GL_TEXTURE1);
		glUniform1i(uTextureFinal, 1);

		glUseProgram(shadowProgram);
		uViewShadow = glGetUniformLocation(shadowProgram, "view");

		glUseProgram(renderProgram);

		uDiffuseMap = glGetUniformLocation(renderProgram, "diffuseMap");
		uShadowMap = glGetUniformLocation(renderProgram, "shadowMap");
		uHasDiffuseMap = glGetUniformLocation(renderProgram, "hasDiffuseMap");
		aTexCoord = glGetAttribLocation(renderProgram, "texCoord");

		glUniform1i(uShadowMap, 1);

		uShadowMapSize = glGetUniformLocation(renderProgram, "shadowMapSize");
		uView = glGetUniformLocation(renderProgram, "currView");

		glUniform1f(uShadowMapSize, shadowMapSize);

		glActiveTexture(GL_TEXTURE1);
		glUniform1i(uDiffuseMap, 1);

		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glEnable(GL_COLOR_MATERIAL);

		shadowRenderBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, shadowRenderBuffer);

		shadowRenderTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, shadowRenderTexture);

		glTexParameterf(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

		for (int i = 0; i < 6; ++i) {
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT, shadowMapSize, shadowMapSize, 0,
					GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					shadowRenderTexture, 0);
		}

		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);

		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (status != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("FBO Error");
		}

		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		GLUtil.init();
		FontUtil.init();

		try {
			ObjLoader loader = new ObjLoader();
			byte[] mtl = Util.readAllBytesFromFile(new File("obj/player.mtl"));
			byte[] obj = Util.readAllBytesFromFile(new File("obj/player.obj"));
			loader.loadMtl(mtl);
			loader.load(obj);
			playerModel = GLUtil.loadObjToList(loader.objects, aTexCoord);
		} catch(IOException e) {
			System.err.println("Failed to load player obj file");
			e.printStackTrace();
		}

	}

	public void render(double partialTick) {

		if (client.levelDirty) {
			worldModel = GLUtil.loadObjToList(client.world.level.loader.objects, aTexCoord);
		}

		++frameCounter;

		if (Display.getWidth() != width || Display.getHeight() != height) {
			width = Display.getWidth();
			height = Display.getHeight();

			if (capabilities.OpenGL32) {

				if (finalRenderTexture != 0) {
					glDeleteTextures(finalRenderTexture);
				}
				if (finalRenderBuffer != 0) {
					glDeleteRenderbuffers(finalRenderBuffer);
				}

				finalRenderTexture = glGenTextures();
				glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, finalRenderTexture);

				glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGBA32F, width, height, false);

				int finalDepthTexture = glGenTextures();
				glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, finalDepthTexture);
				glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_DEPTH_COMPONENT32F, width, height,
						false);

				finalRenderBuffer = glGenFramebuffers();
				glBindFramebuffer(GL_FRAMEBUFFER, finalRenderBuffer);
				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE,
						finalRenderTexture, 0);
				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_MULTISAMPLE,
						finalDepthTexture, 0);

				int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

				if (status != GL_FRAMEBUFFER_COMPLETE) {
					System.out.println("Framebuffer failed: " + status);
				}
			}

			glViewport(0, 0, width, height);
		}

		glRun(partialTick);

		Display.update();

		runInputMouse();

		if (Display.isCloseRequested()) {
			client.running = false;
		}
	}

	public void runInputMouse() {
		if (Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
			isCaptured = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Mouse.setGrabbed(false);
			isCaptured = false;
		}

		if (isCaptured) {
			rotX += -Mouse.getDX();
			rotY += Mouse.getDY();
			if (rotY > 90) {
				rotY = 90;
			}
			if (rotY < -90) {
				rotY = -90;
			}

			double rrotX = Math.toRadians(rotX) / 2;
			double rrotY = Math.toRadians(rotY) / 2;

			client.player.quat = new Quat4(Math.cos(rrotX), 0, Math.sin(rrotX), 0)
					.prod(new Quat4(Math.cos(rrotY), Math.sin(rrotY), 0, 0));
		}

	}

	public void runInput() {

		if (isCaptured) {

			double fmove = 0;
			double smove = 0;
			// double ymove = 0;

			if (Keyboard.isKeyDown(Keyboard.KEY_W))
				fmove -= 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_S))
				fmove += 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_A))
				smove -= 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_D))
				smove += 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				if (client.player.onGround) {
					client.player.velocity = client.player.velocity.add(new Vec3(0, 0.32, 0));
				}
			}

			double sx = Math.sin(Math.toRadians(rotX));
			double cx = Math.cos(Math.toRadians(rotX));

			double mulFactor = 0.01;

			if (client.player.onGround) {
				mulFactor = 0.07;
			}

			client.player.velocity = client.player.velocity
					.add(new Vec3(cx * smove + sx * fmove, 0, -sx * smove + cx * fmove).mul(mulFactor));

			scale *= Math.pow(1.001, Mouse.getDWheel());
			if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
				client.player.position = client.player.position.add(new Vec3(0, 0.4, 0));
				client.player.velocity = new Vec3(client.player.velocity.x, 0, client.player.velocity.z);
			}
		}
	}

	public void glRun(double partialTick) {
		glBindFramebuffer(GL_FRAMEBUFFER, shadowRenderBuffer);
		glViewport(0, 0, shadowMapSize, shadowMapSize);

		glUseProgram(shadowProgram);

		glEnable(GL_DEPTH_TEST);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		double sfov = 2.0 * Math.toDegrees(Math.atan(shadowMapSize / (shadowMapSize - 0.5)));
		GLU.gluPerspective((float) sfov, 1, 0.01f, 1000);
		glGetFloat(GL_PROJECTION_MATRIX, shadowProjection);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		Vec3 newPos = Util.mix(client.player.lastPosition, client.player.position, partialTick)
				.add(client.player.eyeOffset);

		double ang = System.nanoTime() / 10000000000.0 % 1 * 2 * Math.PI;
		// double ang = 0;

		AABB aabb = client.world.level.aabbs.get("Light.0.000");

		if (aabb != null) {
			lightPosition = aabb.max.add(aabb.min).mul(0.5);
		}

		glLight(GL_LIGHT0, GL_DIFFUSE,
				(FloatBuffer) BufferUtils.createFloatBuffer(4).put(1).put(1).put(1).put(1).flip());
		glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) BufferUtils.createFloatBuffer(4).put((float) lightPosition.x)
				.put((float) lightPosition.y).put((float) lightPosition.z).put(1).flip());

		for (int i = 0; i < 6; ++i) {
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					shadowRenderTexture, 0);
			glClear(GL_DEPTH_BUFFER_BIT);

			glPushMatrix();

			// glTranslated(-newPosT.x, -newPosT.y, -newPosT.z);
			Vec3 center = cubemapDirs[i * 2].add(lightPosition);
			Vec3 up = cubemapDirs[i * 2 + 1];
			GLU.gluLookAt((float) lightPosition.x, (float) lightPosition.y, (float) lightPosition.z, (float) center.x,
					(float) center.y, (float) center.z, (float) up.x, (float) up.y, (float) up.z);
			glGetFloat(GL_MODELVIEW_MATRIX, shadowView);
			glUniformMatrix4(uViewShadow, false, shadowView);
			glPopMatrix();

			renderWorld(partialTick);
		}

		glUseProgram(renderProgram);

		if (capabilities.OpenGL32) {
			glBindFramebuffer(GL_FRAMEBUFFER, finalRenderBuffer);

			glClampColor(GL_CLAMP_VERTEX_COLOR, GL_FALSE);
			glClampColor(GL_CLAMP_FRAGMENT_COLOR, GL_FALSE);
		} else {
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		}

		glViewport(0, 0, width, height);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glBindTexture(GL_TEXTURE_CUBE_MAP, shadowRenderTexture);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, (float) width / height, 0.01f, 1000);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glPushMatrix();

		Quat4 newRot = client.player.quat;
		glRotated(Math.toDegrees(Math.acos(newRot.w) * 2), -newRot.x, -newRot.y, -newRot.z);

		glTranslated(-newPos.x, -newPos.y, -newPos.z);

		glGetFloat(GL_MODELVIEW_MATRIX, view);

		float[] array = new float[16];
		shadowProjection.get(array);
		shadowProjection.position(0);

		glUniformMatrix4(uView, false, view);
		glPopMatrix();

		glPushAttrib(GL_ALL_ATTRIB_BITS);
		glPushClientAttrib(GL_ALL_CLIENT_ATTRIB_BITS);
		Sphere sphere = new Sphere();
		glPushMatrix();
		glTranslated(lightPosition.x, lightPosition.y, lightPosition.z);
		glColor3d(0, 0, 0);
		sphere.draw(0.1f, 8, 8);
		glPopMatrix();
		renderWorld(partialTick);
		glPopAttrib();
		glPopClientAttrib();

		if (capabilities.OpenGL32) {
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

		glUseProgram(0);

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glDisable(GL_LIGHT0);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();

		glPushAttrib(GL_ALL_ATTRIB_BITS);
		glPushClientAttrib(GL_ALL_CLIENT_ATTRIB_BITS);
		renderGUI(partialTick);
		glPopAttrib();
		glPopClientAttrib();
	}

	public void renderGUI(double partialTick) {
		// Draw Debug
		FontUtil.drawText("FPS: " + fps + "\nPartial Tick: " + partialTick, FontUtil.font16);

		// Draw Messages (If Any)
		int i = 0;
		String[] msg = { "", "", "" };
		for (Map.Entry<Long, String> entry : client.messageQueue.descendingMap().entrySet())
			if (i < 3)
				msg[i++] = entry.getValue();
		Dimension[] dims = { FontUtil.getTextDimension(msg[0], FontUtil.font36),
				FontUtil.getTextDimension(msg[1], FontUtil.font24),
				FontUtil.getTextDimension(msg[2], FontUtil.font16) };

		glPushMatrix();
		glTranslated(width / 2 - dims[0].getWidth() / 2, 65 + dims[0].getHeight() / 20, 0);
		FontUtil.drawText(msg[0], FontUtil.font36);
		glPopMatrix();
		glPushMatrix();
		glTranslated(width / 2 - dims[1].getWidth() / 2, 30 + dims[1].getHeight() / 20, 0);
		FontUtil.drawText(msg[1], FontUtil.font24);
		glPopMatrix();
		glPushMatrix();
		glTranslated(width / 2 - dims[2].getWidth() / 2, 5 + dims[2].getHeight() / 20, 0);
		FontUtil.drawText(msg[2], FontUtil.font16);
		glPopMatrix();
	}

	public void renderWorld(double partialTick) {
		glUniform1i(uHasDiffuseMap, 0);

		glPushMatrix();
		// Random random = new Random(102);
		// for(int i = -10; i <= 10; ++i) {
		// for(int j = -10; j <= 10; ++j) {
		// if (random.nextInt(5) == 0) {
		// glPushMatrix();
		// glTranslated(i * 2, 0 * 2, j * 2);
		// glCallList(GLUtil.cubeList);
		// glPopMatrix();
		// }
		// }
		// }
		Level level = client.world.level;
		if (worldModel != null) {
			for (RenderObject obj : worldModel.renderObjects) {
				AABB aabb = level.aabbs.get(obj.name);
				if (!aabb.renderable)
					continue;
				// glUniform1i(uHasDiffuseMap, obj.diffuseTexture);
				// glBindTexture(GL_TEXTURE_2D, obj.diffuseTexture);

				ObjLoader.Material mat = obj.material;
				if (aabb.material != null) {
					mat = level.loader.materials.get(aabb.material);
				}

				if (mat != null && mat.diffuse != null) {
					glColor3d(mat.diffuse.x, mat.diffuse.y, mat.diffuse.z);
				}
				glCallList(obj.displayList);
			}
		}
		glPopMatrix();

		glTranslated(0, 0, 0);
		glColor3d(1, 1, 1);

		for (Entity entity : client.world.entities.values()) {
			glPushMatrix();
			Vec3 pos = Util.mix(entity.lastPosition, entity.position, partialTick);
			glTranslated(pos.x, pos.y, pos.z);
			GLUtil.renderObj(playerModel, uHasDiffuseMap);
			glPopMatrix();
		}
	}
}
