package com.redstoner.javautils.busy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;

public class Busy extends Module implements Listener {
	
	private List<Player>		busy			= new ArrayList<>();
	private Map<Player, String>	targets			= new HashMap<>();
	private String[]			aliases			= {"emsg", "msg", "tell", "etell", "w", "ew", "t", "et", "pm", "epm", "m", "whisper", "ewhisper"};
	private String[]			replyAliases	= {"r", "er", "reply", "ereply"};
	
	@Override
	public void onEnable() {
		
	}
	
	//@formatter:off
	@Override
	public String getCmdManagerString() {
		return
		"command busy {"
			+ "perm utils.imbusy;"
			
			+ "on {"
				+ "type player;"
				+ "perm utils.imbusy.use;"
				+ "run busy_on;"
				+ "help Toggles your busy status on;"
			+ "}"
			
			+ "off {"
				+ "type player;"
				+ "perm utils.imbusy.use;"
				+ "run busy_off;"
				+ "help Toggles your busy status off;"
			+ "}"
			
			+ "toggle {"
				+ "type player;"
				+ "perm utils.imbusy.use;"
				+ "run busy_toggle;"
				+ "help Toggles your busy status;"
			+ "}"
			
			+ "status [string:player] {"
				+ "help Checks whether a player is busy;"
				+ "run busy_status player;"
			+ "}"
		+ "}";
	}
	//@formatter:on
	
	@Command(hook = "busy_toggle")
	public void busyToggle(CommandSender sender) {
		Player player = (Player) sender;
		if (busy.contains(player)) {
			busy.remove(player);
			Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RESET + "" + ChatColor.GRAY + " is no longer busy");
		} else {
			busy.add(player);
			Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RESET + "" + ChatColor.GRAY + " is now busy");
		}
	}
	
	@Command(hook = "busy_status")
	public void busyStatus(CommandSender sender, String player) {
		Player p = Bukkit.getPlayer(player);
		if (busy.contains(p)) {
			sender.sendMessage(ChatColor.GREEN + "That player is busy!");
		} else {
			sender.sendMessage(ChatColor.GREEN + "That player is not busy!");
		}
	}
	
	@Command(hook = "busy_on")
	public void busyOn(CommandSender sender) {
		Player player = (Player) sender;
		if (busy.contains(player)) {
			player.sendMessage(ChatColor.RED + "You are already busy!");
			return;
		}
		busy.add(player);
		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RESET + "" + ChatColor.GRAY + " is now busy");
	}
	
	@Command(hook = "busy_off")
	public void busyOff(CommandSender sender) {
		Player player = (Player) sender;
		if (!busy.contains(player)) {
			player.sendMessage(ChatColor.RED + "You aren't currently busy!");
			return;
		}
		busy.remove(player);
		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RESET + "" + ChatColor.GRAY + " is no longer busy");
	}
	
	@EventHandler
	public void onCommandSend(PlayerCommandPreprocessEvent e) {
		String command = e.getMessage().replaceAll("^/", "");
		String[] args = command.split(" ");
		String cmd = args[0];
		if (args.length < 2) {
			return;
		}
		boolean message = false;
		for (String alias : aliases) {
			if (alias.equalsIgnoreCase(cmd) || cmd.equalsIgnoreCase("essentials:" + alias)) {
				message = true;
			}
		}
		if (!message) {
			for (String alias : replyAliases) {
				if (alias.equalsIgnoreCase(cmd) || cmd.equalsIgnoreCase("essentials:" + alias)) {
					for (Player p : busy) {
						if (targets.get(e.getPlayer()) != null && p.getName().toLowerCase().contains(targets.get(e.getPlayer()).toLowerCase())) {
							e.getPlayer().sendMessage(ChatColor.RED + "You may not message " + p.getName() + " at this time, they are busy.");
							e.setCancelled(true);
							return;
						}
					}
					return;
				}
			}
		}
		if (!message || e.getPlayer().hasPermission("utils.imbusy.override")) {
			return;
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().toLowerCase().contains(args[1].toLowerCase())) {
				targets.put(e.getPlayer(), args[1].toLowerCase());
			}
		}
		for (Player p : busy) {
			if (p.getName().toLowerCase().contains(args[1].toLowerCase())) {
				e.getPlayer().sendMessage(ChatColor.RED + "You may not message " + p.getName() + " at this time, they are busy.");
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@Override
	public String getName() {
		return "Busy";
	}
	
	@Override
	public String getDescription() {
		return "Allows you to set a busy state, which can disable private messages";
	}
	
}
