package com.redstoner.javautils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;

public class LagChunks extends Module {

	@Override
	public String getName() {
		return "LagChunks";
	}

	@Override
	public String getDescription() {
		return "Checks for laggy chunks";
	}
	
	private Map<Location, Integer> scan(int amount) {
		Map<Location, Integer> result = new HashMap<>();
		
		for (World world : Bukkit.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				if (chunk.getEntities().length > amount) {
					Location entLoc = chunk.getEntities()[0].getLocation();
					
					result.put(new Location(world, entLoc.getX(), entLoc.getY(), entLoc.getZ()), amount);
				}
			}
		}
		return result;
	}
	
	@Command(hook="scan_cmd")
	public void scan_cmd(CommandSender sender, int amount) {
		Map<Location, Integer> result = scan(amount);
		
		sender.sendMessage("\n" + ChatColor.GREEN + "--- LagChunks ---");
		
		for (Location loc : result.keySet()) {
			sender.sendMessage(ChatColor.AQUA + "Laggy chunk found at: " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + " with " + result.get(loc) + " entities!");
		}
	}

	@Override
	public String getCmdManagerString() {
		return "command lc {"
				+ "[int:amount] {"
					+ "run scan_cmd amount;"
					+ "help scans for laggy chunks;"
					+ "}"
				+ "}";
	}
	
	
}
