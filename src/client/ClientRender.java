// Jacky Liao and Harry Zhang
// October 20, 2017
// Summative
// ICS4U Ms.Strelkovska

package client;

import entity.Entity;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import util.math.Vec3;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;

//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL13.*;
//import static org.lwjgl.opengl.GL14.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.opengl.GL32.*;

public class ClientRender {

	private Random random = new Random();

	public Client client;

	public int frameCounter = 0;
	public float fov = 90;
	public int width;
	public int height;

	public int uSamples;
	public int uTextureFinal;

	public int uDiffuseMap;
	public int uHasDiffuseMap;

	public int aTexCoord;

	public int renderProgram;
	public int postProgram;

	public int finalRenderBuffer;
	public int finalRenderTexture;

	public RenderObjectList playerModel;

	public int samples = 16;

	public double scale = 1;

	public static boolean isCaptured;

	public ClientRender(Client client) {
		this.client = client;
	}

	public void initRendering(int width, int height) throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setResizable(true);
		Display.create();
		Display.setVSyncEnabled(true);

		glInit();
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
			playerModel = GLUtil.loadObj("obj/sinon-sword-art-online.obj", aTexCoord);
		} catch(IOException e) {
			System.out.println("Failed to load texture");
			e.printStackTrace();
		}

	}

	public void render(double partialTick) {

		++frameCounter;

		if (Display.getWidth() != width || Display.getHeight() != height) {
			width = Display.getWidth();
			height = Display.getHeight();

			if(finalRenderTexture != 0) {
				glDeleteTextures(finalRenderTexture);
			}
			if(finalRenderBuffer != 0) {
				glDeleteRenderbuffers(finalRenderBuffer);
			}

			/*finalRenderTexture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, finalRenderTexture);
//			glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_WRAP_S, GL_REPEAT);
//			glTexParameteri(GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_WRAP_T, GL_REPEAT);
//			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

			glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGBA8, width, height, false);

			int finalDepthTexture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, finalDepthTexture);
			glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_DEPTH_COMPONENT24, width, height, false);

			finalRenderBuffer = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, finalRenderBuffer);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, finalRenderTexture, 0);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_MULTISAMPLE, finalDepthTexture, 0);

//			int depthBuffer = glGenRenderbuffers();
//			glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
//			glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT32F, width, height);
//			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);

			int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

			if(status != GL_FRAMEBUFFER_COMPLETE) {
				System.out.println("Framebuffer failed: " + status);
			}*/

			glViewport(0, 0, width, height);
		}

		runInput(partialTick);

		glRun(partialTick);

		Display.update();

		if(Display.isCloseRequested()) {
			client.running = false;
		}
	}

	public void runInput(double partialTick) {
		if(Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
			isCaptured = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Mouse.setGrabbed(false);
			isCaptured = false;
		}

		if(isCaptured) {

			client.player.quat.x += Mouse.getDX();
			client.player.quat.y += Mouse.getDY();
			client.player.quat.y = Math.max(-90, Math.min(90, client.player.quat.y));

			double fmove = 0;
			double smove = 0;
			double ymove = 0;

			if (Keyboard.isKeyDown(Keyboard.KEY_W))
				fmove -= 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_S))
				fmove += 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_A))
				smove -= 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_D))
				smove += 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				ymove += 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				ymove -= 1;

			double yx = Math.cos(Math.toRadians(client.player.quat.x));
			double yy = Math.sin(Math.toRadians(client.player.quat.x));

			client.player.velocity = client.player.velocity.add(new Vec3(smove * yx - fmove * yy, ymove, smove * yy + fmove * yx).mul(0.001));
			scale *= Math.pow(1.001, Mouse.getDWheel());
		}
	}

	public void glRun(double partialTick) {

		//glBindFramebuffer(GL_FRAMEBUFFER, finalRenderBuffer);

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

		glRotated(-client.player.quat.y, 1, 0, 0);
		glRotated(client.player.quat.x, 0, 1, 0);

		Vec3 newPos = client.player.position.sub(client.player.lastPosition).mul(partialTick).add(client.player.lastPosition);
		glTranslated(-newPos.x, -newPos.y, -newPos.z);

		glPushMatrix();
		glTranslated(0, -2, 0);
		glUniform1i(uHasDiffuseMap, 0);
		glCallList(GLUtil.cubeList);
		glPopMatrix();

		glTranslated(0, -1.15, 0);
		glColor3d(1, 1, 1);

		for(Entity entity : client.world.entities.values()) {
			if(entity == client.player) {
				continue;
			}
			glPushMatrix();
			Vec3 pos = entity.position.sub(entity.lastPosition).mul(partialTick).add(entity.lastPosition);
			glTranslated(pos.x, pos.y, pos.z);
			glRotated(-((entity.quat.x - entity.lastQuat.x) * partialTick + entity.lastQuat.x) + 180, 0, 1, 0);
			GLUtil.renderObj(playerModel, uHasDiffuseMap);
			glPopMatrix();
		}

		/*glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
		glEnd();*/
	}
}
