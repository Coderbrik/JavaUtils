package com.redstoner.javautils.chatgroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.javautils.loginsecurity.LoginSecurity;
import com.redstoner.moduleLoader.interfaces.Module;
import com.redstoner.moduleLoader.json.JSONManager;

public class ChatGroups implements Module, Listener {
	Map<Serializable, Serializable> keys;

	private Map<Serializable, Serializable>	groups;
	
	private List<String> cgToggles;
	
	private static final String	KEYS_FILENAME	= "chatgroup_keys.json";
	private static final String	GROUPS_FILENAME	= "chatgroups.json";
	
	private static final String CHAT_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "CG" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
	
	@Override
	public String getName() {
		return "ChatGroups";
	}
	
	@Override
	public String getDescription() {
		return "Makes players able to chat in groups that only some people see.";
	}
	
	@Override
	public boolean onEnable() {
		keys = JSONManager.getConfiguration(KEYS_FILENAME);
		groups = JSONManager.getConfiguration(GROUPS_FILENAME);
		
		cgToggles = new ArrayList<>();
		
		return true;
	}
	
	private void saveKeys() {
		JSONManager.saveConfiguration(keys, KEYS_FILENAME);
	}
	
	private void saveGroups() {
		JSONManager.saveConfiguration(groups, GROUPS_FILENAME);
	}
	
	private String getCgKey(String UUID) {
		if (keys.containsKey(UUID)) {
			return (String) keys.get(UUID);
		}
		
		return ":";
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String uuid = player.getUniqueId().toString();
		
		if (groups.containsKey(uuid) && !LoginSecurity.getLoggingIn().keySet().contains(player.getUniqueId())) {
			boolean format = false;
			
			if (e.getMessage().startsWith(getCgKey(uuid))) {
				e.setMessage(e.getMessage().substring(1));
				format = true;
			} else if (cgToggles.contains(uuid)) {
				format = true;
			}
			
			if (format) {
				String message = CHAT_PREFIX + player.getDisplayName() + ChatColor.RESET + ": " + ChatColor.GOLD + e.getMessage();
				
				for (Player recipient : Bukkit.getOnlinePlayers()) {
					String recipientUuid = recipient.getUniqueId().toString();
					
					if (groups.containsKey(recipientUuid) && groups.get(recipientUuid).equals(groups.get(uuid))) {
						recipient.sendMessage(message);
					}
				}
				e.setCancelled(true);
			}
		}
	}
	
	@Command(hook = "cgjoin")
	public void cgJoin(CommandSender sender, String name) {
		Player player = (Player) sender;
		
		player.sendMessage(CHAT_PREFIX + ChatColor.GREEN + "Successfully joined \"" + ChatColor.GOLD + name + ChatColor.GREEN + "\"!");
		
		groups.put(player.getUniqueId().toString(), name);
		saveGroups();
	}
	
	@Command(hook = "cgleave")
	public void cgLeave(CommandSender sender) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		
		if (groups.containsKey(uuid)) {
			groups.remove(uuid);
			saveGroups();
		} else {
			player.sendMessage(CHAT_PREFIX + ChatColor.DARK_RED + "You are not in a chat group!");
		}
	}
	
	@Command(hook = "cginfo")
	public void cgInfo(CommandSender sender) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		
		if (!groups.containsKey(uuid)) {
			player.sendMessage(CHAT_PREFIX + ChatColor.DARK_RED + "You are not in a chat group!");
			return;
		}
		
		String curGroup = (String) groups.get(uuid);
		
		List<String> messages = new ArrayList<>();
		String[] msgs = {"\n§2--=[ ChatGroups ]=--", "§2Current group: §a" + curGroup, "§2Users in this group:"};
		messages.addAll(Arrays.asList(msgs));
		
		for (Serializable key : groups.keySet()) {
			if (groups.get(key).equals(curGroup)) {
				messages.add("  - " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer().getDisplayName());
			}
		}
		
		player.sendMessage(messages.toArray(new String[0]));
	}
	
	@Command(hook = "cgkey")
	public void cgKey(CommandSender sender, String key) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		
		if (key.length() != 1) {
			player.sendMessage(CHAT_PREFIX + ChatColor.DARK_RED + "Please enter a single character for the cg key.");
			return;
		}
		
		if (key.equals(":")) {
			keys.remove(uuid);
		} else {
			keys.put(uuid, key);
		}
		
		saveKeys();
		
		player.sendMessage(CHAT_PREFIX + ChatColor.GREEN + "Successfully changed the chat group key to: '" + ChatColor.GOLD + key + ChatColor.GREEN + "'!");
	}
	
	@Command(hook = "cgshowkey")
	public void cgShowKey(CommandSender sender) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		
		player.sendMessage(CHAT_PREFIX + ChatColor.GREEN + "Your current chat group key is: " + ChatColor.GOLD + getCgKey(uuid) + ChatColor.GREEN + "'!");
	}
	
	@Command(hook = "cgtoggle")
	public void cgToggle(CommandSender sender, String nil) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		
		if (cgToggles.contains(uuid)) {
			cgToggles.remove(uuid);
			player.sendMessage(CHAT_PREFIX + ChatColor.GREEN + "Successfully disabled the chat group toggle!");
		} else {
			cgToggles.add(uuid);
			player.sendMessage(CHAT_PREFIX + ChatColor.GREEN + "Successfully enabled the chat group toggle!");
		}
	}
}
