// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package client;

import entity.Entity;
import entity.EntityPlayer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import util.AABB;
import util.Util;
import util.math.Quat4;
import util.math.Vec3;
import world.Level;

import javax.swing.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

// Class for rendering all the graphics
public class ClientRender {

	public ClientHandler client;

	public int frameCounter = 0;
	public int fps = 0;
	public float fov = 90;
	public int width;
	public int height;

	public static double sensitivity = 1;

	public int uSamples;
	public int uTextureFinal;

	public int uShadowMap;
	public int uDiffuseMap;
	public int uHasDiffuseMap;
	public int uShadowMapSize;
	public int uView;

	public int uViewShadow;

	// Transform matrices
	public FloatBuffer shadowProjection = BufferUtils.createFloatBuffer(16);
	public FloatBuffer shadowView = BufferUtils.createFloatBuffer(16);
	public FloatBuffer view = BufferUtils.createFloatBuffer(16);

	// Cubemap directions for shadow
	public Vec3[] cubemapDirs = { new Vec3(1.0, 0.0, 0.0), new Vec3(0.0, -1.0, 0.0), new Vec3(-1.0, 0.0, 0.0),
			new Vec3(0.0, -1.0, 0.0), new Vec3(0.0, 1.0, 0.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, -1.0, 0.0),
			new Vec3(0.0, 0.0, -1.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, -1.0, 0.0), new Vec3(0.0, 0.0, -1.0),
			new Vec3(0.0, -1.0, 0.0), };

	// Position of the light
	public Vec3 lightPosition = new Vec3();

	public int aTexCoord;

	// Shaders
	public int renderProgram;
	public int postProgram;
	public int shadowProgram;

	// Post processing for antialiasing
	public int finalRenderBuffer;
	public int finalDepthTexture;
	public int finalRenderTexture;

	// Shadow buffer
	public int shadowRenderBuffer;
	public int shadowRenderTexture;

	public int shadowMapSize = 1024;

	// Models for the world and the player
	public RenderObjectList worldModel, playerModel;

	// Orientation of the player
	public double rotX;
	public double rotY;

	// Antialiasing samples
	public int samples = 4;

	public double scale = 1;

	// Some flags for user input
	public boolean isCaptured;
	public boolean isDebugOpen;
	public boolean isGUIOpen;
	public boolean vsync = true;

	public static boolean advancedOpenGL = true;

	public static String openglVersion;

	// List of buttons that the user can click on
	public ArrayList<Button> buttons = new ArrayList<>();

	public ContextCapabilities capabilities;

	public ClientRender(ClientHandler client) {
		this.client = client;
	}

	// Start the rendering
	public void initRendering(int width, int height) throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setResizable(true);
		Display.create();
		Display.setVSyncEnabled(true);

		openglVersion = glGetString(GL_VERSION);
		System.out.println(openglVersion);

		capabilities = GLContext.getCapabilities();

		if (!capabilities.OpenGL11) {
			throw new LWJGLException("OpenGL 1.1 is not supported.");
		}

		// Prompt the user to see whether they want to go for OpenGL 3.2
		int res = JOptionPane.showConfirmDialog(null, "Use OpenGL 3.2?" + (capabilities.OpenGL32 ? "" : "\nYour computer doesn't seem to support it. Try it anyway?"), "OpenGL 3.2", JOptionPane.YES_NO_OPTION);
		advancedOpenGL = res == JOptionPane.YES_OPTION;

		// Start OpenGL related things
		glInit();

		// Start a frame counter
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

	// Start OpenGL
	public void glInit() {

		glClearColor(0.5f, 0.5f, 1.0f, 1);

		if(advancedOpenGL) {

			// Load all shaders
			renderProgram = GLUtil.loadProgram("/shaders/world.vert", "/shaders/world.frag");
			postProgram = GLUtil.loadProgram("/shaders/post.vert", "/shaders/post.frag");
			shadowProgram = GLUtil.loadProgram("/shaders/shadow.vert", "/shaders/shadow.frag");

			// Find uniform locations, and set them
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
		}

		// Some OpenGL flags
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glEnable(GL_COLOR_MATERIAL);

		if(advancedOpenGL) {
			// Generate the shadow mapping textures
			shadowRenderBuffer = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, shadowRenderBuffer);

			shadowRenderTexture = glGenTextures();
			glBindTexture(GL_TEXTURE_CUBE_MAP, shadowRenderTexture);

			glTexParameterf(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameterf(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

			// Bind a texture for each face of the cubemap
			for (int i = 0; i < 6; ++i) {
				glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT, shadowMapSize, shadowMapSize, 0,
						GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
						shadowRenderTexture, 0);
			}

			// No draw buffer, only depth attachment
			glDrawBuffer(GL_NONE);
			glReadBuffer(GL_NONE);

			// Error check
			int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
			if (status != GL_FRAMEBUFFER_COMPLETE) {
				System.err.println("FBO Error");
			}

			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		}

		// Initialize GL utilities
		GLUtil.init();

		// Initialize font utilities
		FontUtil.init();

		// Add a bunch of buttons for the GUI
		buttons.add(new Button(-100, 140, FontUtil.font36, "-", (b) -> {
			sensitivity /= 1.2;
		}));

		buttons.add(new Button(100, 140, FontUtil.font36, "+", (b) -> {
			sensitivity *= 1.2;
		}));

		buttons.add(new Button(0, 240, FontUtil.font24, "On", (b) -> {
			vsync = !vsync;
			Display.setVSyncEnabled(vsync);
			b.label = vsync ? "On" : "Off";
		}));

		if(advancedOpenGL) {
			buttons.add(new Button(-100, 340, FontUtil.font36, "-", (b) -> {
				samples--;
				if (samples < 1) {
					samples = 1;
				} else {
					resizeTextures();
				}
			}));

			buttons.add(new Button(100, 340, FontUtil.font36, "+", (b) -> {
				samples++;
				if (samples > 16) {
					samples = 16;
				} else {
					resizeTextures();
				}
			}));
		}

		// Load the player obj
		try {
			ObjLoader loader = new ObjLoader();
			byte[] mtl = Util.readAllBytesFromStream(ClientRender.class.getResourceAsStream("/obj/player.mtl"));
			byte[] obj = Util.readAllBytesFromStream(ClientRender.class.getResourceAsStream("/obj/player.obj"));
			loader.loadMtl(mtl);
			loader.load(obj);
			playerModel = GLUtil.loadObjToList(loader.objects, aTexCoord);
		} catch (IOException e) {
			System.err.println("Failed to load player obj file");
			e.printStackTrace();
		}

	}

	// Regenerate the post processing textures
	public void resizeTextures() {
		if (advancedOpenGL) {

			// Delete previous textures
			if (finalRenderTexture != 0) {
				glDeleteTextures(finalRenderTexture);
			}

			if (finalDepthTexture != 0) {
				glDeleteTextures(finalDepthTexture);
			}

			if (finalRenderBuffer != 0) {
				glDeleteRenderbuffers(finalRenderBuffer);
			}

			glUseProgram(postProgram);
			glUniform1i(uSamples, samples);
			glUseProgram(0);

			// Create framebuffers and textures
			finalRenderTexture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, finalRenderTexture);

			glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGBA32F, width, height, false);

			finalDepthTexture = glGenTextures();
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
	}

	// Render the frame
	public void render(double partialTick) {

		// Load the level from the obj if the level has changed
		if (client.levelDirty) {
			GLUtil.cleanUp(worldModel);
			worldModel = GLUtil.loadObjToList(client.world.level.loader.objects, aTexCoord);
			client.levelDirty = false;
		}

		++frameCounter;

		// Resize if display size changed
		if (Display.getWidth() != width || Display.getHeight() != height) {
			width = Display.getWidth();
			height = Display.getHeight();

			resizeTextures();

			glViewport(0, 0, width, height);
		}

		// Run render functions
		glRun(partialTick);

		// Update the frame
		Display.update();

		// Process mouse input
		runInputMouse();

		// Terminate if closed
		if (Display.isCloseRequested()) {
			client.running = false;
		}
	}

	// Process mouse input
	public void runInputMouse() {

		// Capture mouse input
		if(!isGUIOpen && !isCaptured && Mouse.isButtonDown(0)) {
			isCaptured = true;
			Mouse.setGrabbed(true);
		}

		// Process keyboard keybinds
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_F3) {
					// Toggle debug info
					isDebugOpen = !isDebugOpen;
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					// Open options
					isGUIOpen = !isGUIOpen;
					if(isGUIOpen) {
						isCaptured = false;
						Mouse.setGrabbed(false);
					} else {
						isCaptured = true;
						Mouse.setGrabbed(true);
					}
				} else if(Keyboard.getEventKey() == Keyboard.KEY_F11) {
					// Fullscreen
					try {
						if(Display.isFullscreen()) {
							Display.setFullscreen(false);
							Display.setResizable(true);
						} else {
							Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
						}
					} catch(LWJGLException e) {
						System.err.println("Cannot set fullscreen");
						e.printStackTrace();
					}
				}
			}
		}

		// Process mouse events
		while(Mouse.next()) {
			if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
				if(isGUIOpen) {
					// Process mouse events on the buttons
					for (int i = buttons.size() - 1; i >= 0; --i) {
						Button button = buttons.get(i);
						if (button.isWithin(Mouse.getX(), height - Mouse.getY(), width)) {
							button.listener.clicked(button);
						}
					}
				}
			}
		}

		// Rotate the player camera if the mouse is captured
		if (isCaptured) {
			rotX += -Mouse.getDX() * sensitivity;
			rotY += Mouse.getDY() * sensitivity;
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

	// Run player movement input
	public void runInput() {

		if (isCaptured) {

			double fmove = 0;
			double smove = 0;
			double ymove = 0;

			// WASD + Space + Shift
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
				ymove += 1;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				ymove -= 1;
			}

			double sx = Math.sin(Math.toRadians(rotX));
			double cx = Math.cos(Math.toRadians(rotX));

			double mulFactor = 0.01;

			if (client.player.onGround) {
				mulFactor = 0.07;
			}

			// Change player velocity
			client.player.velocity = client.player.velocity
					.add(new Vec3(cx * smove + sx * fmove, client.player.spectate ? ymove : 0, -sx * smove + cx * fmove).mul(mulFactor));

			scale *= Math.pow(1.001, Mouse.getDWheel());
		}
	}

	// GL functions to render frame
	public void glRun(double partialTick) {

		// Position of player at this frame
		Vec3 newPos = Util.mix(client.player.lastPosition, client.player.position, partialTick)
				.add(client.player.eyeOffset);

		if(advancedOpenGL) {
			// Render to shadow map if available
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
		}

		AABB aabb = client.world.level.aabbs.get("Light.0.000");

		if (aabb != null) {
			lightPosition = aabb.max.add(aabb.min).mul(0.5);
		}

		if(advancedOpenGL) {
			// Actual rendering

			glLight(GL_LIGHT0, GL_DIFFUSE,
					(FloatBuffer) BufferUtils.createFloatBuffer(4).put(1).put(1).put(1).put(1).flip());
			glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) BufferUtils.createFloatBuffer(4).put((float) lightPosition.x)
					.put((float) lightPosition.y).put((float) lightPosition.z).put(1).flip());

			// Render to each of the 6 faces
			for (int i = 0; i < 6; ++i) {
				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
						shadowRenderTexture, 0);
				glClear(GL_DEPTH_BUFFER_BIT);

				glPushMatrix();

				// glTranslated(-newPosT.x, -newPosT.y, -newPosT.z);
				Vec3 center = cubemapDirs[i * 2].add(lightPosition);
				Vec3 up = cubemapDirs[i * 2 + 1];

				// Apply transform
				GLU.gluLookAt((float) lightPosition.x, (float) lightPosition.y, (float) lightPosition.z, (float) center.x,
						(float) center.y, (float) center.z, (float) up.x, (float) up.y, (float) up.z);
				glGetFloat(GL_MODELVIEW_MATRIX, shadowView);
				glUniformMatrix4(uViewShadow, false, shadowView);
				glPopMatrix();

				// Render the world, depth map only
				renderWorld(partialTick);
			}

			glUseProgram(renderProgram);

			glBindFramebuffer(GL_FRAMEBUFFER, finalRenderBuffer);

			glClampColor(GL_CLAMP_VERTEX_COLOR, GL_FALSE);
			glClampColor(GL_CLAMP_FRAGMENT_COLOR, GL_FALSE);
		}

		// Render to post processing buffer or screen

		glViewport(0, 0, width, height);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if(advancedOpenGL) {
			glBindTexture(GL_TEXTURE_CUBE_MAP, shadowRenderTexture);
		}

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);

		// Perspective matrix
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, (float) width / height, 0.01f, 1000);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		if(advancedOpenGL)
			glPushMatrix();

		// Compute camera rotation
		Quat4 newRot = client.player.quat;
		glRotated(Math.toDegrees(Math.acos(newRot.w) * 2), -newRot.x, -newRot.y, -newRot.z);

		glTranslated(-newPos.x, -newPos.y, -newPos.z);

		// Send view matrix if using shaders, otherwise move the light sources
		if(advancedOpenGL) {
			glGetFloat(GL_MODELVIEW_MATRIX, view);
			glUniformMatrix4(uView, false, view);
			glPopMatrix();
		} else {
			glLight(GL_LIGHT0, GL_DIFFUSE,
					(FloatBuffer) BufferUtils.createFloatBuffer(4).put(1).put(1).put(1).put(1).flip());
			glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) BufferUtils.createFloatBuffer(4).put((float) lightPosition.x)
					.put((float) lightPosition.y).put((float) lightPosition.z).put(1).flip());
		}

		// Actually render the world (onto the postprocessing buffer if available)
		glPushAttrib(GL_ALL_ATTRIB_BITS);
		glPushClientAttrib(GL_ALL_CLIENT_ATTRIB_BITS);
		renderWorld(partialTick);
		glPopAttrib();
		glPopClientAttrib();

		if (advancedOpenGL) {
			// If we're using postprocessing buffer, render this buffer to the screen
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

			// Fullscreen textured quad, drawing the image to the display
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

		if(advancedOpenGL)
			glUseProgram(0);

		// Prepare to render the GUI
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glDisable(GL_LIGHT0);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();

		// Render the GUI
		glPushAttrib(GL_ALL_ATTRIB_BITS);
		glPushClientAttrib(GL_ALL_CLIENT_ATTRIB_BITS);
		renderGUI(partialTick);
		glPopAttrib();
		glPopClientAttrib();
	}

	// Render GUI
	public void renderGUI(double partialTick) {

		// Draw Debug
		if (isDebugOpen) {
			StringBuilder builder = new StringBuilder();
			builder.append("FPS: ").append(fps).append('\n');
			builder.append("Memory: ").append((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576)
					.append("/").append(Runtime.getRuntime().totalMemory() / 1048576).append(" MiB").append('\n');
			builder.append("OpenGL: ").append(openglVersion).append('\n');
			builder.append("Advanced OpenGL: ").append(advancedOpenGL).append('\n');
			builder.append('\n');
			builder.append("Id: ").append(client.player.id).append('\n');
			builder.append("Position: ").append(Util.vectorToString(client.player.position)).append('\n');
			builder.append("Velocity: ").append(Util.vectorToString(client.player.velocity)).append('\n');
			builder.append("On ground: ").append(client.player.onGround).append('\n');
			builder.append("Level: ").append(client.world.level.level).append('\n');
			builder.append("AABBs: ").append(client.world.level.aabbs.size()).append('\n');
			FontUtil.drawText(builder.toString(), FontUtil.font16);
		}

		// Draw Messages (If Any)
		int i = 0;
		String[] msg = { "", "", "" };
		for (Map.Entry<Long, String> entry : client.messageQueue.descendingMap().entrySet())
			if (i < 3)
				msg[i++] = entry.getValue();

		// Draw message text
		FontUtil.drawCenterText(msg[0], FontUtil.font36, width / 2, 80);
		FontUtil.drawCenterText(msg[1], FontUtil.font24, width / 2, 45);
		FontUtil.drawCenterText(msg[2], FontUtil.font16, width / 2, 20);

		// Draw the gui if open
		if(isGUIOpen) {
			// Darken background
			glColor4d(0, 0, 0, 0.75);
			glBegin(GL_QUADS);
			glVertex2d(0, 0);
			glVertex2d(0, height);
			glVertex2d(width, height);
			glVertex2d(width, 0);
			glEnd();

			// Draw the text
			glColor3d(1, 1, 1);
			FontUtil.drawCenterText("Options", FontUtil.font36, width / 2, 50);

			FontUtil.drawCenterText("Mouse sensitivity", FontUtil.font24, width / 2, 100);
			FontUtil.drawCenterText(String.format("%.2f", sensitivity), FontUtil.font24, width / 2, 140);

			FontUtil.drawCenterText("VSync", FontUtil.font24, width / 2, 200);

			if(advancedOpenGL) {
				FontUtil.drawCenterText("Antialiasing samples", FontUtil.font24, width / 2, 300);
				FontUtil.drawCenterText(String.valueOf(samples), FontUtil.font24, width / 2, 340);
			}

			// Render each button
			for(Button button : buttons) {
				button.render(width);
			}
		}
	}

	// Render the world
	public void renderWorld(double partialTick) {
		if(advancedOpenGL)
			glUniform1i(uHasDiffuseMap, 0);

		glPushMatrix();

		// Get the current level
		Level level = client.world.level;
		if (worldModel != null) {
			// Go through each object in the world
			for (RenderObject obj : worldModel.renderObjects) {
				AABB aabb = level.aabbs.get(obj.name);
				// Only render if server says it's renderable
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

				// Render the object stored in the display list
				glCallList(obj.displayList);
			}
		}
		glPopMatrix();

		glColor3d(1, 1, 1);

		// Now draw every player
		for (Entity entity : client.world.entities.values()) {
			EntityPlayer player = (EntityPlayer) entity;
			// Don't draw player if in spectator
			if(player.spectate) {
				continue;
			}
			glPushMatrix();
			Vec3 pos = Util.mix(entity.lastPosition, entity.position, partialTick);

			// Render the player at pos
			glTranslated(pos.x, pos.y, pos.z);
			GLUtil.renderObj(playerModel, uHasDiffuseMap);
			glPopMatrix();
		}
	}
}
