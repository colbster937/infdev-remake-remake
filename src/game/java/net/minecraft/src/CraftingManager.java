package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CraftingManager {
	private static final CraftingManager instance = new CraftingManager();
	private List recipes = new ArrayList();

	public static final CraftingManager getInstance() {
		return instance;
	}

	private CraftingManager() {
		(new RecipesTools()).addRecipes(this);
		(new RecipesWeapons()).addRecipe(this);
		(new RecipesIngots()).addRecipes(this);
		(new RecipesFood()).addRecipes(this);
		(new RecipesCrafting()).addRecipes(this);
		(new RecipesArmor()).addRecipes(this);
		this.addRecipe(new ItemStack(Block.cloth, 1), new Object[]{"###", "###", "###", Character.valueOf('#'), Item.silk});
		this.addRecipe(new ItemStack(Block.tnt, 1), new Object[]{"X#X", "#X#", "X#X", Character.valueOf('X'), Item.gunpowder, Character.valueOf('#'), Block.sand});
		this.addRecipe(new ItemStack(Block.stairSingle, 3), new Object[]{"###", Character.valueOf('#'), Block.cobblestone});
		this.addRecipe(new ItemStack(Block.ladder, 1), new Object[]{"# #", "###", "# #", Character.valueOf('#'), Item.stick});
		this.addRecipe(new ItemStack(Item.door, 1), new Object[]{"##", "##", "##", Character.valueOf('#'), Block.planks});
		this.addRecipe(new ItemStack(Item.sign, 1), new Object[]{"###", "###", " X ", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.stick});
		this.addRecipe(new ItemStack(Block.planks, 4), new Object[]{"#", Character.valueOf('#'), Block.wood});
		this.addRecipe(new ItemStack(Item.stick, 4), new Object[]{"#", "#", Character.valueOf('#'), Block.planks});
		this.addRecipe(new ItemStack(Block.torch, 4), new Object[]{"X", "#", Character.valueOf('X'), Item.coal, Character.valueOf('#'), Item.stick});
		this.addRecipe(new ItemStack(Item.bowlEmpty, 4), new Object[]{"# #", " # ", Character.valueOf('#'), Block.planks});
		this.addRecipe(new ItemStack(Block.minecartTrack, 16), new Object[]{"X X", "X#X", "X X", Character.valueOf('X'), Item.ingotIron, Character.valueOf('#'), Item.stick});
		this.addRecipe(new ItemStack(Item.minecart, 1), new Object[]{"# #", "###", Character.valueOf('#'), Item.ingotIron});
		this.addRecipe(new ItemStack(Item.bucketEmpty, 1), new Object[]{"# #", " # ", Character.valueOf('#'), Item.ingotIron});
		this.addRecipe(new ItemStack(Item.striker, 1), new Object[]{"A ", " B", Character.valueOf('A'), Item.ingotIron, Character.valueOf('B'), Item.flint});
		this.addRecipe(new ItemStack(Item.bread, 1), new Object[]{"###", Character.valueOf('#'), Item.wheat});
		this.addRecipe(new ItemStack(Block.stairCompactWood, 4), new Object[]{"#  ", "## ", "###", Character.valueOf('#'), Block.planks});
		this.addRecipe(new ItemStack(Block.stairCompactStone, 4), new Object[]{"#  ", "## ", "###", Character.valueOf('#'), Block.cobblestone});
		this.addRecipe(new ItemStack(Item.painting, 1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), Item.stick, Character.valueOf('X'), Block.cloth});
		this.addRecipe(new ItemStack(Item.appleGold, 1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), Block.blockGold, Character.valueOf('X'), Item.appleRed});
		Collections.sort(this.recipes, new RecipeSorter(this));
		System.out.println(this.recipes.size() + " recipes");
	}

	void addRecipe(ItemStack var1, Object... var2) {
		String var3 = "";
		int var4 = 0;
		int var5 = 0;
		int var6 = 0;
		if(var2[var4] instanceof String[]) {
			String[] var11 = (String[])((String[])var2[var4++]);

			for(int var8 = 0; var8 < var11.length; ++var8) {
				String var9 = var11[var8];
				++var6;
				var5 = var9.length();
				var3 = var3 + var9;
			}
		} else {
			while(var2[var4] instanceof String) {
				String var7 = (String)var2[var4++];
				++var6;
				var5 = var7.length();
				var3 = var3 + var7;
			}
		}

		HashMap var12;
		int var15;
		for(var12 = new HashMap(); var4 < var2.length; var4 += 2) {
			Character var13 = (Character)var2[var4];
			var15 = 0;
			if(var2[var4 + 1] instanceof Item) {
				var15 = ((Item)var2[var4 + 1]).shiftedIndex;
			} else if(var2[var4 + 1] instanceof Block) {
				var15 = ((Block)var2[var4 + 1]).blockID;
			}

			var12.put(var13, Integer.valueOf(var15));
		}

		int[] var14 = new int[var5 * var6];

		for(var15 = 0; var15 < var5 * var6; ++var15) {
			char var10 = var3.charAt(var15);
			if(var12.containsKey(Character.valueOf(var10))) {
				var14[var15] = ((Integer)var12.get(Character.valueOf(var10))).intValue();
			} else {
				var14[var15] = -1;
			}
		}

		this.recipes.add(new CraftingRecipe(var5, var6, var14, var1));
	}

	public ItemStack findMatchingRecipe(int[] var1) {
		for(int var2 = 0; var2 < this.recipes.size(); ++var2) {
			CraftingRecipe var3 = (CraftingRecipe)this.recipes.get(var2);
			if(var3.matchRecipe(var1)) {
				return var3.createResult(var1);
			}
		}

		return null;
	}
}
