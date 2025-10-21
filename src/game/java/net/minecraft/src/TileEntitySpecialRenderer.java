package net.minecraft.src;

import net.peyton.eagler.minecraft.FontRenderer;

public abstract class TileEntitySpecialRenderer {
	protected TileEntityRenderer tileEntityRenderer;

	public abstract void renderTileEntityMobSpawner(TileEntity var1, double var2, double var4, double var6, float var8);

	protected void bindTextureByName(String var1) {
		RenderEngine var2 = this.tileEntityRenderer.renderEngine;
		var2.bindTexture(var2.getTexture(var1));
	}

	public void setTileEntityRenderer(TileEntityRenderer var1) {
		this.tileEntityRenderer = var1;
	}

	public FontRenderer getFontRenderer() {
		return this.tileEntityRenderer.getFontRenderer();
	}
}
