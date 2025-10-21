package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

import net.peyton.eagler.minecraft.suppliers.EntitySupplier;

public class EntityList {
	private static Map stringToClassMapping = new HashMap();
	private static Map classToStringMapping = new HashMap();

	private static void addMapping(Class var0, EntitySupplier var3, String var1) {
		stringToClassMapping.put(var1, var3);
		classToStringMapping.put(var0, var1);
	}

	public static Entity createEntityInWorld(String var0, World var1) {
		EntitySupplier var2 = (EntitySupplier) stringToClassMapping.get(var0);
		return var2 != null ? var2.createEntity(var1) : null;
	}

	public static Entity createEntityFromNBT(NBTTagCompound var0, World var1) {
		Entity var2 = createEntityInWorld(var0.getString("id"), var1);
		if (var2 != null) var2.readFromNBT(var0);
		else System.out.println("Skipping Entity with id " + var0.getString("id"));
		return var2;
	}

	public static String getEntityString(Entity var0) {
		return (String)classToStringMapping.get(var0.getClass());
	}

	static {
		addMapping(EntityArrow.class, EntityArrow::new, "Arrow");
		addMapping(EntityItem.class, EntityItem::new, "Item");
		addMapping(EntityPainting.class, EntityPainting::new, "Painting");
		addMapping(EntityLiving.class, EntityLiving::new, "Mob");
		addMapping(EntityMonster.class, EntityMonster::new, "Monster");
		addMapping(EntityCreeper.class, EntityCreeper::new, "Creeper");
		addMapping(EntitySkeleton.class, EntitySkeleton::new, "Skeleton");
		addMapping(EntitySpider.class, EntitySpider::new, "Spider");
		addMapping(EntityGiant.class, EntityGiant::new, "Giant");
		addMapping(EntityZombie.class, EntityZombie::new, "Zombie");
		addMapping(EntityPig.class, EntityPig::new, "Pig");
		addMapping(EntitySheep.class, EntitySheep::new, "Sheep");
		addMapping(EntityTNTPrimed.class, EntityTNTPrimed::new, "PrimedTnt");
		addMapping(EntityFallingSand.class, EntityFallingSand::new, "FallingSand");
		addMapping(EntityMinecart.class, EntityMinecart::new, "Minecart");
	}
}
