package net.minecraft.src;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

public class GuiDeleteWorld extends GuiCreateWorld {
	public GuiDeleteWorld(GuiScreen var1) {
		super(var1);
		this.screenHeader = "Delete world";
	}

	public void initButtons() {
		this.controlList.add(new GuiButton(6, this.width / 2 - 100, this.height / 6 + 168, "Cancel"));
	}

	public void actionPerformed(int var1) {
		String var2 = this.getSaveFileName(var1);
		if(var2 != null) {
			this.mc.displayGuiScreen(new GuiYesNo(this, "Are you sure you want to delete this world?", "\'" + var2 + "\' will be lost forever!", var1));
		}

	}

	public void deleteWorld(boolean var1, int var2) {
		if(var1) {
			VFile2 var3 = Minecraft.getMinecraftDir();
			World.deleteWorld(var3, this.getSaveFileName(var2));
		}

		this.mc.displayGuiScreen(this.parentGuiScreen);
	}
}
