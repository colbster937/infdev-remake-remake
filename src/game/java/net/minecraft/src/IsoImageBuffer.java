package net.minecraft.src;

import net.lax1dude.eaglercraft.opengl.ImageData;

public class IsoImageBuffer {
	public ImageData image;
	public World level;
	public int chunkX;
	public int chunkZ;
	public boolean rendered = false;
	public boolean noContent = false;
	public int lastVisible = 0;
	public boolean addedToRenderQueue = false;

	public IsoImageBuffer(World var1, int var2, int var3) {
		this.level = var1;
		this.setChunkPosition(var2, var3);
	}

	public void setChunkPosition(int var1, int var2) {
		this.rendered = false;
		this.chunkX = var1;
		this.chunkZ = var2;
		this.lastVisible = 0;
		this.addedToRenderQueue = false;
	}

	public void setWorldAndChunkPosition(World var1, int var2, int var3) {
		this.level = var1;
		this.setChunkPosition(var2, var3);
	}
}
