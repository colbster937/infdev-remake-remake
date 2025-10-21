package net.minecraft.src;

public class ItemSword extends Item {
	private int weaponDamage;

	public ItemSword(int var1, int var2) {
		super(var1);
		this.maxStackSize = 1;
		this.maxDamage = 32 << var2;
		this.weaponDamage = 4 + var2 * 2;
	}

	public float getStrVsBlock(ItemStack var1, Block var2) {
		return 1.5F;
	}

	public void hitEntity(ItemStack var1, EntityLiving var2) {
		var1.damageItem(1);
	}

	public void onBlockDestroyed(ItemStack var1, int var2, int var3, int var4, int var5) {
		var1.damageItem(2);
	}

	public int getDamageVsEntity(Entity var1) {
		return this.weaponDamage;
	}
}
