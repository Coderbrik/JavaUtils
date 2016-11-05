package com.redstoner.javautils.blockplacemods.mods;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ModBooleanCauldron extends ModBooleanAbstract {
	
	@Override
	public String getName() {
		return "Cauldron";
	}
	
	@Override
	public String getSimpleDescription() {
		return null;
	}
	
	@Override
	public String getDetailedDescription() {
		return null;
	}
	
	@Override
	protected boolean enabledByDefault() {
		return false;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking() && hasEnabled(event.getPlayer()) && event.getClickedBlock().getType() == Material.CAULDRON) {
			Block block = event.getClickedBlock();
			block.setData((byte) ((block.getData() + 1) % 3));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType() == Material.CAULDRON && !event.getPlayer().isSneaking() && hasEnabled(event.getPlayer())) {
			event.getBlock().setData((byte) 3);
		}
	}
	
}
