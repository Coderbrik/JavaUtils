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
import com.redstoner.moduleLoader.Module;
import com.redstoner.moduleLoader.ModuleLoader;

public class Mentio extends Module implements Listener {
	private Map<String, String[]> mentions;
	private File configFile;
	
	@Override
	public String getName() {
		return "Mentio";
	}
	
	@Override
	public String getDescription() {
		return "Lets you pick words which notify you when someone says them in the chat.";
	}
	
	@Override
	public void onEnable() {
		ModuleLoader loader = ModuleLoader.getLoader();
		configFile = new File(loader.getConfigFolder(), "Mentio.json");
		
		if (!configFile.exists()) {
			try {
				FileWriter writer = new FileWriter(configFile);
				
				writer.write("{}");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not create blank mentions file! Disabling.");
				
				enabled = false;
				return;
			}
			
		}
		
		mentions = new HashMap<>();
		
		try {
			FileReader reader = new FileReader(configFile);
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			
			for (Object key : json.keySet()) {
				JSONArray mentionsJSONArray = (JSONArray) json.get(key);
				String[] mentionsArray = (String[]) mentionsJSONArray.toArray();
				
				mentions.put((String) key, mentionsArray);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not get mentions from file! Disabling.");
			
			enabled = false;
		}
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
	
	@Command(hook = "addWord")
	public void addWord(CommandSender sender, String word) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		List<String> newMentions = new ArrayList<>();
		String[] playerMentions = mentions.get(uuid);
		String[] oldMentions = new String[0];
		
		if (playerMentions != null) {
			oldMentions = playerMentions.clone();
			newMentions = Arrays.asList(playerMentions);
		}
		
		if (newMentions.size() == 0) {
			newMentions.add(player.getName().toLowerCase());
			newMentions.add(player.getDisplayName().toLowerCase());
		}
		
		newMentions.add(word.toLowerCase());
		mentions.put(uuid, newMentions.toArray(new String[0]));
		
		if (!saveMentions()) {
			player.sendMessage(ChatColor.RED + "Could not save mentions! Please contact an admin!");
			
			if (oldMentions.length > 0) {
				mentions.put(uuid, oldMentions);
			} else {
				mentions.remove(uuid);
			}
			
			return;
		}
		
		player.sendMessage(ChatColor.GREEN + "Successfully added mention: " + ChatColor.YELLOW + word);
	}
	
	@Command(hook = "delWord")
	public void delWord(CommandSender sender, String word) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		List<String> newMentions = new ArrayList<>();
		String[] playerMentions = mentions.get(uuid);
		String[] oldMentions = new String[0];
		
		if (playerMentions != null) {
			oldMentions = playerMentions.clone();
			newMentions = Arrays.asList(playerMentions);
		}
		
		if (!newMentions.contains(word.toLowerCase())) {
			player.sendMessage(ChatColor.RED + "You do not have \"" + word + "\" added as a mention!");
			return;
		}
		
		newMentions.remove(word.toLowerCase());
		mentions.put(uuid, newMentions.toArray(new String[0]));
		
		if (!saveMentions()) {
			player.sendMessage(ChatColor.RED + "Could not save mentions! Please contact an admin!");
			
			if (oldMentions.length > 0) {
				mentions.put(uuid, oldMentions);
			} else {
				mentions.remove(uuid);
			}
			
			return;
		}
		
		player.sendMessage(ChatColor.GREEN + "Successfully removed mention: " + ChatColor.YELLOW + word);
	}
	
	@Command(hook = "listWords")
	public void listWords(CommandSender sender) {
		Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		String[] playerMentions = mentions.get(uuid);
		
		player.sendMessage(ChatColor.GREEN + "Words you are currently listening for: (case ignored)");
		
		if (playerMentions == null || playerMentions.length == 0) {
			player.sendMessage(ChatColor.RED + " - " + ChatColor.DARK_AQUA + player.getName().toLowerCase());
			player.sendMessage(ChatColor.RED + " - " + ChatColor.DARK_AQUA + player.getDisplayName().toLowerCase());
			return;
		}
		
		for (String word : playerMentions) {
			player.sendMessage(ChatColor.RED + " - " + ChatColor.DARK_AQUA + word);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent e) {
		List<String> words = new ArrayList<>();
		words.addAll(Arrays.asList(e.getMessage().split(" ")));
		
		for (Player recipient : e.getRecipients()) {
			String uuid = recipient.getUniqueId().toString();
			String[] listenWords;
			
			if (mentions.containsKey(uuid)) {
				listenWords = mentions.get(uuid);
			} else {
				listenWords = new String[] {recipient.getName(), ChatColor.stripColor(recipient.getDisplayName())};
			}
			
			List<String> wordsCopy = new ArrayList<>(words);
			boolean isMentioned = false;
			
			for (String listenWord : listenWords) {
				for (int i = 0; i < wordsCopy.size(); i++) {
					String word = wordsCopy.get(i);
					
					if (word.toLowerCase().contains(listenWord.toLowerCase())) {
						isMentioned = true;
						String formatting = "";
						
						for (int j = 0; j <= words.indexOf(word); j++) {
							formatting += words.get(j);
						}
						
						formatting = findAllColors(formatting);
						
						wordsCopy.set(i, ChatColor.GREEN + "" + ChatColor.ITALIC + ChatColor.stripColor(word) + formatting);
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
				
				String message = String.join(" ", wordsCopy.toArray(new String[0]));
				recipient.sendMessage(e.getFormat().replace("%1$s", e.getPlayer().getDisplayName()).replace("%2$s", message));
				recipient.playSound(recipient.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 2);
			}
		}
	}
}
