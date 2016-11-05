package com.redstoner.javautils.blockplacemods;

import com.redstoner.javautils.blockplacemods.saving.Saveable;
import com.redstoner.javautils.blockplacemods.saving.SubSaveable;
import org.bukkit.inventory.ItemStack;

public class InventoryData extends SubSaveable {
	
	private final ItemStack[] stacks;
	
	public InventoryData(Saveable superSaveable, int size) {
		super(superSaveable);
		this.stacks = new ItemStack[size];
	}
	
	public InventoryData(Saveable superSaveable, ItemStack[] stacks) {
		super(superSaveable);
		this.stacks = stacks;
	}
	
	public int getSize() {
		return stacks.length;
	}
	
	public ItemStack[] getStacks() {
		return stacks;
	}
	
}
