package com.redstoner.javautils.warn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;

public class warn extends Module implements Listener {
	
	
	@Override
	public void onEnable() {

	}
	
	@Override
	public String getCmdManagerString() {
		return "command warn {"
			+ "	perm utils.warn;"
			+ "	[string:name] {"
			+ "		run warn name;"
			+ "	}"
			+ "}"
			+ "command warnp {"
			+ " [string:name] {"
			+ "		run warn_p name;"
			+ "		type player;"
			+ " }"
			+ "}";
			
	}
	
	
	@Command(hook = "warn")
	public void warn_normal(CommandSender sender, String name){
		Player s = (Player) sender;
		Bukkit.broadcastMessage(ChatColor.AQUA + " = "+ ChatColor.DARK_GREEN + ChatColor.BOLD+ "Lag incomming! "+ ChatColor.WHITE + "-" +s.getDisplayName());
		;
	}
	@Command(hook = "warn_p")
	public void warn_possible(CommandSender sender, String name){
		Player s = (Player) sender;
		Bukkit.broadcastMessage(ChatColor.AQUA + " = "+ ChatColor.DARK_GREEN + ChatColor.BOLD+ "Possible lag incomming! "+ ChatColor.WHITE + "-" +s.getDisplayName());
	}

	@Override
	public String getName() {
		return "warn";
	}

	@Override
	public String getDescription() {
		return "Warns players for incomming lag.";
	}
	
	
	
}
