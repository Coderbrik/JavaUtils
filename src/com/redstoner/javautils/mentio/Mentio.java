package com.redstoner.javautils.mentio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.interfaces.Module;
import com.redstoner.moduleLoader.misc.FolderRegistry;

public class Mentio implements Module, Listener {
	private Map<String, List<String>>	mentions;
	private File						configFile;
	
	@Override
	public String getName() {
		return "Mentio";
	}
	
	@Override
	public String getDescription() {
		return "Lets you pick words which notify you when someone says them in the chat.";
	}
	
	@Override
	public boolean onEnable() {
		configFile = new File(FolderRegistry.configFolder, "Mentio.json");
		
		if (!configFile.exists()) {
			try {
				FileWriter writer = new FileWriter(configFile);
				
				writer.write("{}");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not create blank mentions file! Disabling.");
				
				return false;
			}
			
		}
		
		mentions = new HashMap<>();
		
		try {
			FileReader reader = new FileReader(configFile);
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			
			for (Object key : json.keySet()) {
				JSONArray mentionsJSONArray = (JSONArray) json.get(key);
				List<String> mentionsArray = new ArrayList<String>();
				
				for (Object obj : mentionsJSONArray.toArray()) {
					mentionsArray.add(obj.toString());
				}
				
				mentions.put((String) key, mentionsArray);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not get mentions from file! Disabling.");
			
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean saveMentions() {
		JSONObject data = new JSONObject();
		
		for (String key : mentions.keySet()) {
			JSONArray array = new JSONArray();
			
			for (String mention : mentions.get(key)) {
				array.add(mention);
			}
			
			data.put(key, array);
		}
		
		try {
			PrintWriter writer = new PrintWriter(configFile);
			
			writer.write(data.toJSONString());
			writer.close();
			
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String findAllColors(String s) {
		String colors = "";
		
		for (int i = s.indexOf("§"); i >= 0; i = s.indexOf("§", i + 1)) {
			colors += "§" + s.charAt(i + 1);
		}
		
		return colors;
	}
	
	private String getDisplayNameWithoutSuffix(Player player) {
		return ChatColor.stripColor(player.getDisplayName().toLowerCase()).replaceAll("\\$", "").replaceAll("•", "").trim();
	}
	
	@Command(hook = "addWord")
	public void addWord(CommandSender sender, String word) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		List<String> playerMentions = mentions.get(uuid);
		
		List<String> oldMentions = new ArrayList<String>();
		List<String> newMentions = new ArrayList<String>();
		
		if (playerMentions == null) {
			newMentions.add(player.getName().toLowerCase());
			newMentions.add(getDisplayNameWithoutSuffix(player));
		} else {
			oldMentions.addAll(playerMentions);
			newMentions.addAll(playerMentions);
		}
		
		newMentions.add(word.toLowerCase());
		mentions.put(uuid, newMentions);
		
		if (!saveMentions()) {
			player.sendMessage(ChatColor.RED + "Could not save mentions! Please contact an admin!");
			
			if (playerMentions == null) {
				mentions.remove(uuid);
			} else {
				mentions.put(uuid, oldMentions);
			}
			
			return;
		}
		
		player.sendMessage(ChatColor.GREEN + "Successfully added mention: " + ChatColor.YELLOW + word);
	}
	
	@Command(hook = "delWord")
	public void delWord(CommandSender sender, String word) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		List<String> playerMentions = mentions.get(uuid);
		
		List<String> oldMentions = new ArrayList<String>();
		List<String> newMentions = new ArrayList<String>();
		
		if (playerMentions == null) {
			newMentions.add(player.getName().toLowerCase());
			newMentions.add(getDisplayNameWithoutSuffix(player));
		} else {
			oldMentions.addAll(playerMentions);
			newMentions.addAll(playerMentions);
		}
		
		if (!oldMentions.contains(word.toLowerCase())) {
			player.sendMessage(ChatColor.RED + "You do not have \"" + word + "\" added as a mention!");
			return;
		}
		
		newMentions.remove(word.toLowerCase());
		mentions.put(uuid, newMentions);
		
		if (!saveMentions()) {
			player.sendMessage(ChatColor.RED + "Could not save mentions! Please contact an admin!");
			
			if (playerMentions == null) {
				mentions.remove(uuid);
			} else {
				mentions.put(uuid, oldMentions);
			}
			
			return;
		}
		
		player.sendMessage(ChatColor.GREEN + "Successfully removed mention: " + ChatColor.YELLOW + word);
	}
	
	@Command(hook = "listWords")
	public void listWords(CommandSender sender) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		List<String> playerMentions = mentions.get(uuid);
		
		player.sendMessage(ChatColor.GREEN + "Words you are currently listening for (case ignored):");
		
		if (playerMentions == null) {
			player.sendMessage(ChatColor.RED + " - " + ChatColor.DARK_AQUA + player.getName().toLowerCase());
			player.sendMessage(ChatColor.RED + " - " + ChatColor.DARK_AQUA + getDisplayNameWithoutSuffix(player));
			return;
		}
		
		if (playerMentions.size() == 0) {
			player.sendMessage(ChatColor.GREEN + "  You are currently not litstening for any words!");
			return;
		}
		
		for (String word : playerMentions) {
			player.sendMessage(ChatColor.RED + " - " + ChatColor.DARK_AQUA + word);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent e) {
		List<String> words = new ArrayList<String>(Arrays.asList(e.getMessage().split(" ")));
		
		for (Player recipient : e.getRecipients()) {
			String uuid = recipient.getUniqueId().toString();
			List<String> playerMentions = mentions.get(uuid);
			
			if (playerMentions == null) {
				playerMentions = new ArrayList<String>();
				playerMentions.add(recipient.getName());
				playerMentions.add(getDisplayNameWithoutSuffix(recipient));
			}
			
			List<String> mentioColoredWords = new ArrayList<String>(words);
			boolean isMentioned = false;
			
			for (String listenWord : playerMentions) {
				for (int i = 0; i < mentioColoredWords.size(); i++) {
					String word = mentioColoredWords.get(i);
					
					if (word.toLowerCase().contains(listenWord.toLowerCase())) {
						isMentioned = true;
						String formatting = "";
						
						for (int j = 0; j <= words.indexOf(word); j++) {
							formatting += words.get(j);
						}
						
						formatting = findAllColors(formatting);
						
						mentioColoredWords.set(i, ChatColor.GREEN + "" + ChatColor.ITALIC + ChatColor.stripColor(word) + formatting);
					}
				}
			}
			
			if (isMentioned) {
				try {
					e.getRecipients().remove(recipient);
				} catch (UnsupportedOperationException ex) {
					ex.printStackTrace();
					continue;
				}
				
				String message = String.join(" ", mentioColoredWords.toArray(new String[0]));
				recipient.sendMessage(e.getFormat().replace("%1$s", e.getPlayer().getDisplayName()).replace("%2$s", message));
				recipient.playSound(recipient.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 2);
			}
		}
	}
}
