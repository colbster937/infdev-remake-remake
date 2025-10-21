package net.minecraft.src;

import java.util.Random;

public class BlockFurnace extends BlockContainer {
	private final boolean isActive;

	protected BlockFurnace(int var1, boolean var2) {
		super(var1, Material.rock);
		this.isActive = var2;
		this.blockIndexInTexture = 45;
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		this.setDefaultDirection(var1, var2, var3, var4);
	}

	private void setDefaultDirection(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3, var4 - 1);
		int var6 = var1.getBlockId(var2, var3, var4 + 1);
		int var7 = var1.getBlockId(var2 - 1, var3, var4);
		int var8 = var1.getBlockId(var2 + 1, var3, var4);
		byte var9 = 3;
		if(Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var6]) {
			var9 = 3;
		}

		if(Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var5]) {
			var9 = 2;
		}

		if(Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var8]) {
			var9 = 5;
		}

		if(Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var7]) {
			var9 = 4;
		}

		var1.setBlockMetadataWithNotify(var2, var3, var4, var9);
	}

	public int getBlockTextureGeneric(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		if(var5 == 1) {
			return Block.stone.blockIndexInTexture;
		} else if(var5 == 0) {
			return Block.stone.blockIndexInTexture;
		} else {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			return var5 != var6 ? this.blockIndexInTexture : (this.isActive ? this.blockIndexInTexture + 16 : this.blockIndexInTexture - 1);
		}
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		if(this.isActive) {
			int var6 = var1.getBlockMetadata(var2, var3, var4);
			float var7 = (float)var2 + 0.5F;
			float var8 = (float)var3 + 0.0F + var5.nextFloat() * 6.0F / 16.0F;
			float var9 = (float)var4 + 0.5F;
			float var10 = 0.52F;
			float var11 = var5.nextFloat() * 0.6F - 0.3F;
			if(var6 == 4) {
				var1.spawnParticle("smoke", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
				var1.spawnParticle("flame", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
			} else if(var6 == 5) {
				var1.spawnParticle("smoke", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
				var1.spawnParticle("flame", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0D, 0.0D, 0.0D);
			} else if(var6 == 2) {
				var1.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0D, 0.0D, 0.0D);
				var1.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0D, 0.0D, 0.0D);
			} else if(var6 == 3) {
				var1.spawnParticle("smoke", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0D, 0.0D, 0.0D);
				var1.spawnParticle("flame", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0D, 0.0D, 0.0D);
			}

		}
	}

	public int getBlockTextureFromSide(int var1) {
		return var1 == 1 ? Block.stone.blockID : (var1 == 0 ? Block.stone.blockID : (var1 == 3 ? this.blockIndexInTexture - 1 : this.blockIndexInTexture));
	}

	public boolean blockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		TileEntityFurnace var6 = (TileEntityFurnace)var1.getBlockTileEntity(var2, var3, var4);
		var5.displayGUIFurnace(var6);
		return true;
	}

	public static void updateFurnaceBlockState(boolean var0, World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		TileEntity var6 = var1.getBlockTileEntity(var2, var3, var4);
		if(var0) {
			var1.setBlockWithNotify(var2, var3, var4, Block.stoneOvenActive.blockID);
		} else {
			var1.setBlockWithNotify(var2, var3, var4, Block.stoneOvenIdle.blockID);
		}

		var1.setBlockMetadataWithNotify(var2, var3, var4, var5);
		var1.setBlockTileEntity(var2, var3, var4, var6);
	}

	protected TileEntity getBlockEntity() {
		return new TileEntityFurnace();
	}
}
