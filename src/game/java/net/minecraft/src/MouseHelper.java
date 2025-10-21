package net.minecraft.src;

import org.lwjgl.input.Mouse;

import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;

public class MouseHelper {
	public int deltaX;
	public int deltaY;

	public MouseHelper() {
		IntBuffer var2 = GLAllocation.createIntBuffer(1);
		var2.put(0);
		var2.flip();

	}

	public void grabMouseCursor() {
		Mouse.setGrabbed(true);

		this.mouseXYChange();
		this.deltaX = 0;
		this.deltaY = 0;

	}

	public void ungrabMouseCursor() {
		Mouse.setGrabbed(false);

	}

	public void mouseXYChange() {
		this.deltaX = Mouse.getDX();
		this.deltaY = Mouse.getDY();

	}
}
