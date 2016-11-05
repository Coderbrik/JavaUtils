package com.redstoner.javautils.blockplacemods.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nemez.cmdmgr.Command;
import com.redstoner.javautils.blockplacemods.InventoryData;
import com.redstoner.javautils.blockplacemods.PlayerData;
import com.redstoner.javautils.blockplacemods.util.CommandException;
import com.redstoner.javautils.blockplacemods.util.ItemProperties;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public abstract class ModInventoryAbstract extends ModAbstract<InventoryData> {
	
	{
		cmdManagerString.append("clear {");
		cmdManagerString.append("  run clear;");
		cmdManagerString.append("}");
		cmdManagerString.append("[int:slot] {");
		cmdManagerString.append("  run set slot;");
		cmdManagerString.append("}");
	}
	
	private final int SLOTS = getInventoryType().getDefaultSize();
	
	protected abstract InventoryType getInventoryType();
	
	@Override
	protected InventoryData createDefaultData(PlayerData entryFor) {
		return new InventoryData(entryFor, SLOTS);
	}
	
	@Override
	public InventoryData loadDataFor(PlayerData data, JsonReader reader) throws IOException {
		reader.beginArray();
		ItemStack[] stacks = new ItemStack[SLOTS];
		for (int i = 0; i < SLOTS && reader.hasNext(); i++) {
			ItemProperties props = new ItemProperties();
			props.loadFrom(reader);
			stacks[i] = props.toItemStack();
		}
		reader.endArray();
		return new InventoryData(data, stacks);
	}
	
	@Override
	public void saveDataTo(JsonWriter writer, InventoryData data) throws IOException {
		writer.beginArray();
		for (ItemStack stack : data.getStacks()) {
			new ItemProperties(stack).writeTo(writer);
		}
		writer.endArray();
	}
	
	@Command(hook = "clear")
	protected void onClear(Player sender) {
		handleMessage(sender, () -> handleClear(sender));
	}
	
	private String handleClear(Player sender) throws CommandException {
		return reset(sender) ? "Reset data successfully" : "&cYou had no data set!";
	}
	
	@Command(hook = "set")
	protected void onSet(Player sender, int slot) {
		handleMessage(sender, () -> handleSet(sender, slot));
	}
	
	private String handleSet(Player sender, int slot) throws CommandException {
		if (slot >= SLOTS) {
			throw new CommandException("Slot number " + slot + " is too high for " + getInventoryType().toString().toLowerCase() + "s");
		}
		
		InventoryData data = getAndEnsureSaved(sender);
		ItemStack stack = sender.getItemOnCursor();
		if (stack.getType() == Material.AIR) {
			stack = null;
		}
		data.getStacks()[slot] = stack;
		data.scheduleSave();
		String item = stack == null ? "nothing" : stack.getAmount() + " " + stack.getType().toString().toLowerCase().replace("_", "");
		return "Set the item in slot " + slot + " to " + item;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (present(event.getPlayer())) {
			BlockState state = event.getBlock().getState();
			if (state instanceof InventoryHolder) {
				Inventory inv = ((InventoryHolder) state).getInventory();
				if (inv.getType() == getInventoryType()) {
					InventoryData data = get(event.getPlayer());
					inv.setContents(data.getStacks());
					state.update();
				}
			}
			
		}
	}
	
}
