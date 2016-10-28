package com.redstoner.javautils.chatGroups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.javautils.loginSecurity.LoginSecurity;
import com.redstoner.moduleLoader.Module;
import com.redstoner.moduleLoader.ModuleLoader;

public class ChatGroups extends Module implements Listener {
	ModuleLoader				loader;
	private Map<String, String>	keys, groups;
	
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
	public void onEnable() {
		loader = ModuleLoader.getLoader();
		
		keys = loader.getConfiguration(KEYS_FILENAME);
		groups = loader.getConfiguration(GROUPS_FILENAME);
		
		cgToggles = new ArrayList<>();
	}
	
	private void saveKeys() {
		loader.saveConfiguration(keys, KEYS_FILENAME);
	}
	
	private void saveGroups() {
		loader.saveConfiguration(groups, GROUPS_FILENAME);
	}
	
	private String getCgKey(String UUID) {
		if (keys.containsKey(UUID)) {
			return keys.get(UUID);
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
				e.setFormat(CHAT_PREFIX + " " + player.getDisplayName() + ChatColor.RESET + ": " + ChatColor.GOLD);
				Set<Player> recipients = e.getRecipients();
				
				for (Player p : recipients) {
					recipients.remove(p);
				}
				
				for (String key : groups.keySet()) {
					if (groups.get(key).equals(groups.get(uuid))) {
						recipients.add(Bukkit.getPlayer(UUID.fromString(key)));
					}
				}
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
		
		String curGroup = groups.get(uuid);
		
		List<String> messages = new ArrayList<>();
		String[] msgs = {"\n§2--=[ ChatGroups ]=--", "§2Current group: §a" + curGroup, "§2Users in this group:"};
		messages.addAll(Arrays.asList(msgs));
		
		for (String key : groups.keySet()) {
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
	
	//@formatter:off - turns off formatter (has to be a comment)
		@Override
		public String getCmdManagerString() {
			return
				  "command chatgroup {"
					+ "join [string:name] {"
						+ "run cgjoin name;"
						+ "help Joins the specified chat group.;"
						+ "type player;"
					+ "}"
					
					+ "leave {"
						+ "run cgleave;"
						+ "help Leaves the group you are currently in.;"
						+ "type player;"
					+ "}"
					
					+ "info {"
						+ "run cginfo;"
						+ "help Shows information about the group you are currently in.;"
						+ "type player;"
					+ "}"
					
					+ "key {"
						+ "run cgshowkey;"
						+ "help Shows your current chatgroup key.;"
						+ "type player;"
					+ "}"
				+ "}"
				
				+ "command cgt {"
					+ "[string:nil] {"
						+ "run cgtoggle nil;"
						+ "help Toggles sending all messages to chatgroups.;"
						+ "type player;"
					+ "}"
				+ "}"
				
				+ "command cgkey {"
					+ "[string:key] {"
						+ "run cgkey key;"
						+ "help Sets your chatgroup key to the specified one.;"
						+ "type player;"
					+ "}"
				+ "}";
		}
	//@formatter:on - turns on formatter (has to be a comment)
}
