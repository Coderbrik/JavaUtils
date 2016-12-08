package com.redstoner.javautils.warn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.interfaces.Module;

public class Warn implements Module {
	@Command(hook = "warn")
	public void warn_normal(CommandSender sender, String name) {
		Player s = (Player) sender;
		Bukkit.broadcastMessage(ChatColor.AQUA + " = " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Lag incomming! " + ChatColor.RESET + "-" + s.getDisplayName());
	}
	
	@Command(hook = "warnp")
	public void warn_possible(CommandSender sender, String name) {
		Player s = (Player) sender;
		Bukkit.broadcastMessage(ChatColor.AQUA + " = " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Possible lag incomming! " + ChatColor.RESET + "-" + s.getDisplayName());
	}
	
	@Override
	public String getName() {
		return "Warn";
	}
	
	@Override
	public String getDescription() {
		return "Warns players about incomming lag.";
	}
}
