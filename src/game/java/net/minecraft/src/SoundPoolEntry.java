package net.minecraft.src;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

public class SoundPoolEntry {
	public String soundName;
	public VFile2 soundFile;

	public SoundPoolEntry(String var1, VFile2 var2) {
		this.soundName = var1;
		this.soundFile = var2;
	}
}
