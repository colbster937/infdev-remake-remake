package net.minecraft.src;

import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.peyton.eagler.minecraft.Tessellator;

public class EntityRenderer {
	private Minecraft mc;
	private boolean isDisplayActive = false;
	private float farPlaneDistance = 0.0F;
	public ItemRenderer itemRenderer;
	private int rendererUpdateCount;
	private Entity pointedEntity = null;
	private int mouseDX;
	private int mouseDY;
	private Random random = new Random();
	volatile int unusedInt1 = 0;
	volatile int unusedInt2 = 0;
	FloatBuffer fogColorBuffer = GLAllocation.createFloatBuffer(16);
	float fogColorRed;
	float fogColorGreen;
	float fogColorBlue;
	private float prevFogColor;
	private float fogColor;

	public EntityRenderer(Minecraft var1) {
		this.mc = var1;
		this.itemRenderer = new ItemRenderer(var1);
	}

	public void updateRenderer() {
		this.prevFogColor = this.fogColor;
		float var1 = this.mc.theWorld.getBrightness(MathHelper.floor_double(this.mc.thePlayer.posX), MathHelper.floor_double(this.mc.thePlayer.posY), MathHelper.floor_double(this.mc.thePlayer.posZ));
		float var2 = (float)(3 - this.mc.gameSettings.renderDistance) / 3.0F;
		float var3 = var1 * (1.0F - var2) + var2;
		this.fogColor += (var3 - this.fogColor) * 0.1F;
		++this.rendererUpdateCount;
		this.itemRenderer.updateEquippedItem();
		if(this.mc.isRaining) {
			this.addRainParticles();
		}

	}

	private Vec3D getPlayerPosition(float var1) {
		EntityPlayerSP var2 = this.mc.thePlayer;
		double var3 = var2.prevPosX + (var2.posX - var2.prevPosX) * (double)var1;
		double var5 = var2.prevPosY + (var2.posY - var2.prevPosY) * (double)var1;
		double var7 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double)var1;
		return Vec3D.createVector(var3, var5, var7);
	}

	private void getMouseOver(float var1) {
		EntityPlayerSP var2 = this.mc.thePlayer;
		float var3 = var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * var1;
		float var4 = var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * var1;
		Vec3D var5 = this.getPlayerPosition(var1);
		float var6 = MathHelper.cos(-var4 * ((float)Math.PI / 180.0F) - (float)Math.PI);
		float var7 = MathHelper.sin(-var4 * ((float)Math.PI / 180.0F) - (float)Math.PI);
		float var8 = -MathHelper.cos(-var3 * ((float)Math.PI / 180.0F));
		float var9 = MathHelper.sin(-var3 * ((float)Math.PI / 180.0F));
		float var10 = var7 * var8;
		float var12 = var6 * var8;
		double var13 = (double)this.mc.playerController.getBlockReachDistance();
		Vec3D var15 = var5.addVector((double)var10 * var13, (double)var9 * var13, (double)var12 * var13);
		this.mc.objectMouseOver = this.mc.theWorld.rayTraceBlocks(var5, var15);
		double var16 = var13;
		var5 = this.getPlayerPosition(var1);
		if(this.mc.objectMouseOver != null) {
			var16 = this.mc.objectMouseOver.hitVec.distanceTo(var5);
		}

		if(this.mc.playerController instanceof PlayerControllerCreative) {
			var13 = 32.0D;
		} else {
			if(var16 > 3.0D) {
				var16 = 3.0D;
			}

			var13 = var16;
		}

		var15 = var5.addVector((double)var10 * var13, (double)var9 * var13, (double)var12 * var13);
		this.pointedEntity = null;
		List var18 = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, var2.boundingBox.addCoord((double)var10 * var13, (double)var9 * var13, (double)var12 * var13));
		double var19 = 0.0D;

		for(int var21 = 0; var21 < var18.size(); ++var21) {
			Entity var22 = (Entity)var18.get(var21);
			if(var22.canBeCollidedWith()) {
				float var23 = 0.1F;
				AxisAlignedBB var24 = var22.boundingBox.expand((double)var23, (double)var23, (double)var23);
				MovingObjectPosition var25 = var24.calculateIntercept(var5, var15);
				if(var25 != null) {
					double var26 = var5.distanceTo(var25.hitVec);
					if(var26 < var19 || var19 == 0.0D) {
						this.pointedEntity = var22;
						var19 = var26;
					}
				}
			}
		}

		if(this.pointedEntity != null && !(this.mc.playerController instanceof PlayerControllerCreative)) {
			this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity);
		}

	}

	private float getFOVModifier(float var1) {
		EntityPlayerSP var2 = this.mc.thePlayer;
		float var3 = 70.0F;
		if(var2.isInsideOfMaterial(Material.water)) {
			var3 = 60.0F;
		}

		if (this.mc.currentScreen == null) {
			if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindZoom.keyCode)) {
				var3 /= 4.0F;
			}
		}


		if(var2.health <= 0) {
			float var4 = (float)var2.deathTime + var1;
			var3 /= (1.0F - 500.0F / (var4 + 500.0F)) * 2.0F + 1.0F;
		}

		return var3;
	}

	private void hurtCameraEffect(float var1) {
		EntityPlayerSP var2 = this.mc.thePlayer;
		float var3 = (float)var2.hurtTime - var1;
		float var4;
		if(var2.health <= 0) {
			var4 = (float)var2.deathTime + var1;
			GL11.glRotatef(40.0F - 8000.0F / (var4 + 200.0F), 0.0F, 0.0F, 1.0F);
		}

		if(var3 >= 0.0F) {
			var3 /= (float)var2.maxHurtTime;
			var3 = MathHelper.sin(var3 * var3 * var3 * var3 * (float)Math.PI);
			var4 = var2.attackedAtYaw;
			GL11.glRotatef(-var4, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-var3 * 14.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(var4, 0.0F, 1.0F, 0.0F);
		}
	}

	private void setupViewBobbing(float var1) {
		if(!this.mc.gameSettings.thirdPersonView) {
			EntityPlayerSP var2 = this.mc.thePlayer;
			float var3 = var2.distanceWalkedModified - var2.prevDistanceWalkedModified;
			float var4 = var2.distanceWalkedModified + var3 * var1;
			float var5 = var2.prevCameraYaw + (var2.cameraYaw - var2.prevCameraYaw) * var1;
			float var6 = var2.prevCameraPitch + (var2.cameraPitch - var2.prevCameraPitch) * var1;
			GL11.glTranslatef(MathHelper.sin(var4 * (float)Math.PI) * var5 * 0.5F, -Math.abs(MathHelper.cos(var4 * (float)Math.PI) * var5), 0.0F);
			GL11.glRotatef(MathHelper.sin(var4 * (float)Math.PI) * var5 * 3.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(Math.abs(MathHelper.cos(var4 * (float)Math.PI + 0.2F) * var5) * 5.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);
		}
	}

	private void orientCamera(float var1) {
		EntityPlayerSP var2 = this.mc.thePlayer;
		double var3 = var2.prevPosX + (var2.posX - var2.prevPosX) * (double)var1;
		double var5 = var2.prevPosY + (var2.posY - var2.prevPosY) * (double)var1;
		double var7 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double)var1;
		if(this.mc.gameSettings.thirdPersonView) {
			double var9 = 4.0D;
			double var11 = (double)(-MathHelper.sin(var2.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(var2.rotationPitch / 180.0F * (float)Math.PI)) * var9;
			double var13 = (double)(MathHelper.cos(var2.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(var2.rotationPitch / 180.0F * (float)Math.PI)) * var9;
			double var15 = (double)(-MathHelper.sin(var2.rotationPitch / 180.0F * (float)Math.PI)) * var9;

			for(int var17 = 0; var17 < 8; ++var17) {
				float var18 = (float)((var17 & 1) * 2 - 1);
				float var19 = (float)((var17 >> 1 & 1) * 2 - 1);
				float var20 = (float)((var17 >> 2 & 1) * 2 - 1);
				var18 *= 0.1F;
				var19 *= 0.1F;
				var20 *= 0.1F;
				MovingObjectPosition var21 = this.mc.theWorld.rayTraceBlocks(Vec3D.createVector(var3 + (double)var18, var5 + (double)var19, var7 + (double)var20), Vec3D.createVector(var3 - var11 + (double)var18 + (double)var20, var5 - var15 + (double)var19, var7 - var13 + (double)var20));
				if(var21 != null) {
					double var22 = var21.hitVec.distanceTo(Vec3D.createVector(var3, var5, var7));
					if(var22 < var9) {
						var9 = var22;
					}
				}
			}

			GL11.glTranslatef(0.0F, 0.0F, (float)(-var9));
		} else {
			GL11.glTranslatef(0.0F, 0.0F, -0.1F);
		}

		GL11.glRotatef(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * var1, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * var1 + 180.0F, 0.0F, 1.0F, 0.0F);
	}

	private void setupCameraTransform(float var1, int var2) {
		this.farPlaneDistance = (float)(256 >> this.mc.gameSettings.renderDistance);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		float var3 = 0.07F;
		if(this.mc.gameSettings.anaglyph) {
			GL11.glTranslatef((float)(-(var2 * 2 - 1)) * var3, 0.0F, 0.0F);
		}

		GLU.gluPerspective(this.getFOVModifier(var1), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		if(this.mc.gameSettings.anaglyph) {
			GL11.glTranslatef((float)(var2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
		}

		this.hurtCameraEffect(var1);
		if(this.mc.gameSettings.viewBobbing) {
			this.setupViewBobbing(var1);
		}

		this.orientCamera(var1);
	}

	private void renderHand(float var1, int var2) {
		GL11.glLoadIdentity();
		if(this.mc.gameSettings.anaglyph) {
			GL11.glTranslatef((float)(var2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
		}

		GL11.glPushMatrix();
		this.hurtCameraEffect(var1);
		if(this.mc.gameSettings.viewBobbing) {
			this.setupViewBobbing(var1);
		}

		if(!this.mc.gameSettings.thirdPersonView) {
			this.itemRenderer.renderItemInFirstPerson(var1);
		}

		GL11.glPopMatrix();
		if(!this.mc.gameSettings.thirdPersonView) {
			this.itemRenderer.renderOverlays(var1);
			this.hurtCameraEffect(var1);
		}

		if(this.mc.gameSettings.viewBobbing) {
			this.setupViewBobbing(var1);
		}

	}

	public void updateCameraAndRender(float var1) {
		if(this.isDisplayActive && !Display.isActive()) {
			this.mc.displayInGameMenu();
		}

		this.isDisplayActive = Display.isActive();
		int var3;
		int var9;
		int var10;
		if(this.mc.inGameHasFocus) {
			this.mc.mouseHelper.mouseXYChange();
			int var2 = this.mc.mouseHelper.deltaX;
			var3 = this.mc.mouseHelper.deltaY;
			byte var4 = 1;
			if(this.mc.gameSettings.invertMouse) {
				var4 = -1;
			}

			if(this.mouseDX != 0) {
				this.mouseDX = 0;
			}

			if(this.mouseDY != 0) {
				this.mouseDY = 0;
			}

			if(var2 != 0) {
				this.mouseDX = var2;
			}

			if(var3 != 0) {
				this.mouseDY = var3;
			}

			this.mc.thePlayer.setAngles((float)var2, (float)(var3 * var4));
		}

		if(!this.mc.skipRenderWorld) {
			ScaledResolution var7 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
			var3 = var7.getScaledWidth();
			int var8 = var7.getScaledHeight();
			var9 = Mouse.getX() * var3 / this.mc.displayWidth;
			var10 = var8 - Mouse.getY() * var8 / this.mc.displayHeight - 1;
			if(this.mc.theWorld != null) {
				this.renderWorld(var1);
				this.mc.ingameGUI.renderGameOverlay(var1, this.mc.currentScreen != null, var9, var10);
			} else {
				GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
				GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				this.setupOverlayRendering();
			}

			if(this.mc.currentScreen != null) {
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				this.mc.currentScreen.drawScreen(var9, var10, var1);
			}

		}
	}

	public void renderWorld(float var1) {
		this.getMouseOver(var1);
		EntityPlayerSP var2 = this.mc.thePlayer;
		RenderGlobal var3 = this.mc.renderGlobal;
		EffectRenderer var4 = this.mc.effectRenderer;
		double var5 = var2.lastTickPosX + (var2.posX - var2.lastTickPosX) * (double)var1;
		double var7 = var2.lastTickPosY + (var2.posY - var2.lastTickPosY) * (double)var1;
		double var9 = var2.lastTickPosZ + (var2.posZ - var2.lastTickPosZ) * (double)var1;

		for(int var11 = 0; var11 < 2; ++var11) {
			if(this.mc.gameSettings.anaglyph) {
				if(var11 == 0) {
					GL11.glColorMask(false, true, true, false);
				} else {
					GL11.glColorMask(true, false, false, false);
				}
			}

			GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
			this.updateFogColor(var1);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
			GL11.glEnable(GL11.GL_CULL_FACE);
			this.setupCameraTransform(var1, var11);
			ClippingHelperImplementation.getInstance();
			if(this.mc.gameSettings.renderDistance < 2) {
				this.setupFog(-1);
				var3.renderSky(var1);
			}

			GL11.glEnable(GL11.GL_FOG);
			this.setupFog(1);
			Frustrum var12 = new Frustrum();
			var12.setPosition(var5, var7, var9);
			this.mc.renderGlobal.clipRenderersByFrustrum(var12, var1);
			this.mc.renderGlobal.updateRenderers(var2, false);
			this.setupFog(0);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
			RenderHelper.disableStandardItemLighting();
			var3.sortAndRender(var2, 0, (double)var1);
			RenderHelper.enableStandardItemLighting();
			var3.renderEntities(this.getPlayerPosition(var1), var12, var1);
			var4.renderLitParticles(var2, var1);
			RenderHelper.disableStandardItemLighting();
			this.setupFog(0);
			var4.renderParticles(var2, var1);
			if(this.mc.objectMouseOver != null && var2.isInsideOfMaterial(Material.water)) {
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				var3.drawBlockBreaking(var2, this.mc.objectMouseOver, 0, var2.inventory.getCurrentItem(), var1);
				var3.drawSelectionBox(var2, this.mc.objectMouseOver, 0, var2.inventory.getCurrentItem(), var1);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}

			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.setupFog(0);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
			if(this.mc.gameSettings.fancyGraphics) {
				// GL11.glColorMask(false, false, false, false);
				int var13 = var3.sortAndRender(var2, 1, (double)var1);
				// GL11.glColorMask(true, true, true, true);
				if(this.mc.gameSettings.anaglyph) {
					if(var11 == 0) {
						GL11.glColorMask(false, true, true, false);
					} else {
						GL11.glColorMask(true, false, false, false);
					}
				}

				if(var13 > 0) {
					var3.renderAllRenderLists(1, (double)var1);
				}
			} else {
				var3.sortAndRender(var2, 1, (double)var1);
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			if(this.mc.objectMouseOver != null && !var2.isInsideOfMaterial(Material.water)) {
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				var3.drawBlockBreaking(var2, this.mc.objectMouseOver, 0, var2.inventory.getCurrentItem(), var1);
				var3.drawSelectionBox(var2, this.mc.objectMouseOver, 0, var2.inventory.getCurrentItem(), var1);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}

			GL11.glDisable(GL11.GL_FOG);
			if(this.mc.isRaining) {
				this.renderRain(var1);
			}

			if(this.pointedEntity != null) {
			}

			this.setupFog(0);
			GL11.glEnable(GL11.GL_FOG);
			var3.renderClouds(var1);
			GL11.glDisable(GL11.GL_FOG);
			this.setupFog(1);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			this.renderHand(var1, var11);
			if(!this.mc.gameSettings.anaglyph) {
				return;
			}
		}

		GL11.glColorMask(true, true, true, false);
	}

	private void addRainParticles() {
		EntityPlayerSP var1 = this.mc.thePlayer;
		World var2 = this.mc.theWorld;
		int var3 = MathHelper.floor_double(var1.posX);
		int var4 = MathHelper.floor_double(var1.posY);
		int var5 = MathHelper.floor_double(var1.posZ);
		byte var6 = 4;

		for(int var7 = 0; var7 < 50; ++var7) {
			int var8 = var3 + this.random.nextInt(var6 * 2 + 1) - var6;
			int var9 = var5 + this.random.nextInt(var6 * 2 + 1) - var6;
			int var10 = var2.getTopSolidOrLiquidBlock(var8, var9);
			int var11 = var2.getBlockId(var8, var10 - 1, var9);
			if(var10 <= var4 + var6 && var10 >= var4 - var6) {
				float var12 = this.random.nextFloat();
				float var13 = this.random.nextFloat();
				if(var11 > 0) {
					this.mc.effectRenderer.addEffect(new EntityRainFX(var2, (double)((float)var8 + var12), (double)((float)var10 + 0.1F) - Block.blocksList[var11].minY, (double)((float)var9 + var13)));
				}
			}
		}

	}

	private void renderRain(float var1) {
		EntityPlayerSP var2 = this.mc.thePlayer;
		World var3 = this.mc.theWorld;
		int var4 = MathHelper.floor_double(var2.posX);
		int var5 = MathHelper.floor_double(var2.posY);
		int var6 = MathHelper.floor_double(var2.posZ);
		Tessellator var7 = Tessellator.instance;
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/rain.png"));
		byte var8 = 5;

		for(int var9 = var4 - var8; var9 <= var4 + var8; ++var9) {
			for(int var10 = var6 - var8; var10 <= var6 + var8; ++var10) {
				int var11 = var3.getTopSolidOrLiquidBlock(var9, var10);
				int var12 = var5 - var8;
				int var13 = var5 + var8;
				if(var12 < var11) {
					var12 = var11;
				}

				if(var13 < var11) {
					var13 = var11;
				}

				float var14 = 2.0F;
				if(var12 != var13) {
					float var15 = ((float)((this.rendererUpdateCount + var9 * 3121 + var10 * 418711) % 32) + var1) / 32.0F;
					double var16 = (double)((float)var9 + 0.5F) - var2.posX;
					double var18 = (double)((float)var10 + 0.5F) - var2.posZ;
					float var20 = MathHelper.sqrt_double(var16 * var16 + var18 * var18) / (float)var8;
					GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - var20 * var20) * 0.7F);
					var7.startDrawingQuads();
					var7.addVertexWithUV((double)(var9 + 0), (double)var12, (double)(var10 + 0), (double)(0.0F * var14), (double)((float)var12 * var14 / 8.0F + var15 * var14));
					var7.addVertexWithUV((double)(var9 + 1), (double)var12, (double)(var10 + 1), (double)(1.0F * var14), (double)((float)var12 * var14 / 8.0F + var15 * var14));
					var7.addVertexWithUV((double)(var9 + 1), (double)var13, (double)(var10 + 1), (double)(1.0F * var14), (double)((float)var13 * var14 / 8.0F + var15 * var14));
					var7.addVertexWithUV((double)(var9 + 0), (double)var13, (double)(var10 + 0), (double)(0.0F * var14), (double)((float)var13 * var14 / 8.0F + var15 * var14));
					var7.addVertexWithUV((double)(var9 + 0), (double)var12, (double)(var10 + 1), (double)(0.0F * var14), (double)((float)var12 * var14 / 8.0F + var15 * var14));
					var7.addVertexWithUV((double)(var9 + 1), (double)var12, (double)(var10 + 0), (double)(1.0F * var14), (double)((float)var12 * var14 / 8.0F + var15 * var14));
					var7.addVertexWithUV((double)(var9 + 1), (double)var13, (double)(var10 + 0), (double)(1.0F * var14), (double)((float)var13 * var14 / 8.0F + var15 * var14));
					var7.addVertexWithUV((double)(var9 + 0), (double)var13, (double)(var10 + 1), (double)(0.0F * var14), (double)((float)var13 * var14 / 8.0F + var15 * var14));
					var7.draw();
				}
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void setupOverlayRendering() {
		ScaledResolution var1 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		int var2 = var1.getScaledWidth();
		int var3 = var1.getScaledHeight();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)var2, (double)var3, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}

	private void updateFogColor(float var1) {
		World var2 = this.mc.theWorld;
		EntityPlayerSP var3 = this.mc.thePlayer;
		float var4 = 1.0F / (float)(4 - this.mc.gameSettings.renderDistance);
		var4 = 1.0F - (float)Math.pow((double)var4, 0.25D);
		Vec3D var5 = var2.getSkyColor(var1);
		float var6 = (float)var5.xCoord;
		float var7 = (float)var5.yCoord;
		float var8 = (float)var5.zCoord;
		Vec3D var9 = var2.getFogColor(var1);
		this.fogColorRed = (float)var9.xCoord;
		this.fogColorGreen = (float)var9.yCoord;
		this.fogColorBlue = (float)var9.zCoord;
		this.fogColorRed += (var6 - this.fogColorRed) * var4;
		this.fogColorGreen += (var7 - this.fogColorGreen) * var4;
		this.fogColorBlue += (var8 - this.fogColorBlue) * var4;
		if(var3.isInsideOfMaterial(Material.water)) {
			this.fogColorRed = 0.02F;
			this.fogColorGreen = 0.02F;
			this.fogColorBlue = 0.2F;
		} else if(var3.isInsideOfMaterial(Material.lava)) {
			this.fogColorRed = 0.6F;
			this.fogColorGreen = 0.1F;
			this.fogColorBlue = 0.0F;
		}

		float var10 = this.prevFogColor + (this.fogColor - this.prevFogColor) * var1;
		this.fogColorRed *= var10;
		this.fogColorGreen *= var10;
		this.fogColorBlue *= var10;
		if(this.mc.gameSettings.anaglyph) {
			float var11 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
			float var12 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
			float var13 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
			this.fogColorRed = var11;
			this.fogColorGreen = var12;
			this.fogColorBlue = var13;
		}

		GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
	}

	private void setupFog(int var1) {
		EntityPlayerSP var2 = this.mc.thePlayer;
		GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
		GL11.glNormal3f(0.0F, -1.0F, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var3;
		float var4;
		float var5;
		float var6;
		float var7;
		float var8;
		if(var2.isInsideOfMaterial(Material.water)) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
			var3 = 0.4F;
			var4 = 0.4F;
			var5 = 0.9F;
			if(this.mc.gameSettings.anaglyph) {
				var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
				var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
				var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
			}
		} else if(var2.isInsideOfMaterial(Material.lava)) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 2.0F);
			var3 = 0.4F;
			var4 = 0.3F;
			var5 = 0.3F;
			if(this.mc.gameSettings.anaglyph) {
				var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
				var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
				var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
			}
		} else {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glFogf(GL11.GL_FOG_START, this.farPlaneDistance * 0.25F);
			GL11.glFogf(GL11.GL_FOG_END, this.farPlaneDistance);
			if(var1 < 0) {
				GL11.glFogf(GL11.GL_FOG_START, 0.0F);
				GL11.glFogf(GL11.GL_FOG_END, this.farPlaneDistance * 0.8F);
			}
		}

		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
	}

	private FloatBuffer setFogColorBuffer(float var1, float var2, float var3, float var4) {
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(var1).put(var2).put(var3).put(var4);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}
}
