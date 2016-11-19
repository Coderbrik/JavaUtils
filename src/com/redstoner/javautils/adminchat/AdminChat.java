package com.redstoner.javautils.adminchat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.simple.parser.ParseException;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;
import com.redstoner.moduleLoader.ModuleLoader;

public class AdminChat extends Module implements Listener {
	public static List<String>		ac_toggle_list	= new ArrayList<>();
	private Map<UUID, Character>	keys			= new HashMap<>();
	
	@Override
	public String getDescription() {
		return "Allows staff to chat secretly";
	}
	
	@Override
	public String getName() {
		return "AdminChat";
	}
	
	@Override
	public void onEnable() {
		File file = new File(ModuleLoader.getLoader().getConfigFolder(), "adminchat.json");
		if (file.exists()) {
			try {
				Map<String, String> uuids = ModuleLoader.loadMap(file);
				for (Entry<String, String> entry : uuids.entrySet()) {
					keys.put(UUID.fromString(entry.getKey()), entry.getValue().toCharArray()[0]);
				}
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.getPlayer().hasPermission("utils.ac")) {
			if (keys.get(e.getPlayer().getUniqueId()) != null) {
				String string = Character.toString(keys.get(e.getPlayer().getUniqueId()));
				if (e.getMessage().startsWith(string)) {
					e.setCancelled(true);
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.hasPermission("utils.ac")) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AC" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + e.getPlayer().getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', e.getMessage().substring(1)));
						}
					}
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AC" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + e.getPlayer().getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', e.getMessage()).substring(1));
				}
			} else if (e.getMessage().startsWith(",")) {
				e.setCancelled(true);
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.hasPermission("utils.ac")) {
						player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AC" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + e.getPlayer().getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', e.getMessage().substring(1)));
					}
				}
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AC" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + e.getPlayer().getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', e.getMessage()).substring(1));
			} else {
				if (ac_toggle_list.contains(e.getPlayer().getName())) {
					e.setCancelled(true);
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.hasPermission("utils.ac")) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AC" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + e.getPlayer().getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', e.getMessage()));
						}
					}
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AC" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + e.getPlayer().getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.translateAlternateColorCodes('&', e.getMessage()));
				}
			}
		}
	}
	
	@Override
	public void onDisable() {
		Map<String, String> entries = new HashMap<>();
		for (Entry<UUID, Character> entry : keys.entrySet()) {
			entries.put(entry.getKey().toString(), Character.toString(entry.getValue()));
		}
		File file = new File(ModuleLoader.getLoader().getConfigFolder(), "adminchat.json");
		try {
			ModuleLoader.saveMap(file, entries);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Command(hook = "act")
	public void toggleAdminChat(CommandSender sender, String not) {
		String name = sender.getName();
		if (sender instanceof Player) {
			if (ac_toggle_list.contains(name)) {
				ac_toggle_list.remove(name);
				sender.sendMessage("§8[§cAC§8] §aAC toggle: off");
			} else {
				ac_toggle_list.add(name);
				sender.sendMessage("§8[§cAC§8] §aAC toggle: on");
			}
		} else {
			sender.sendMessage("§cOnly players can use this command.");
		}
	}
	
	@Command(hook = "ac_msg")
	public void sendAdminChatMessage(CommandSender sender, String message) {
		String name = sender.getName();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			name = player.getDisplayName();
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("utils.ac")) {
				player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AC" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + name + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', message));
			}
		}
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "AC" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + name + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', message));
	}
	
	@Command(hook = "ackey")
	public void setKey(CommandSender sender, String key) {
		Player player = (Player) sender;
		if (key.length() != 1) {
			player.sendMessage(ChatColor.RED + "Your AdminChat key must be a single character!");
			return;
		}
		char acKey = key.toCharArray()[0];
		player.sendMessage(ChatColor.GREEN + "AdminChat key set to " + key + "!");
		keys.put(player.getUniqueId(), acKey);
	}
}
