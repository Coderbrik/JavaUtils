package com.redstoner.javautils.adminnotes;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.parser.ParseException;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.interfaces.Module;
import com.redstoner.moduleLoader.json.JSONManager;
import com.redstoner.moduleLoader.misc.FolderRegistry;

public class AdminNotes implements Module, Listener {
	
	File file = new File(FolderRegistry.configFolder, "adminnotes.json");
	
	@Override
	public String getDescription() {
		return "Allows staff to leave notes";
	}
	
	@Override
	public String getName() {
		return "AdminNotes";
	}
	
	@Override
	public boolean onEnable() {
		if (file.exists()) {
			try {
				List<Serializable> strings = JSONManager.loadList(file);
				List<Note> notes = new ArrayList<>();
				for (Serializable string : strings) {
					notes.add(Note.fromString((String) string));
				}
				Note.notes = notes;
				
				return true;
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission("utils.adminnotes")) {
			if (Note.notes.size() > 0) {
				e.getPlayer().sendMessage(ChatColor.RED + "There are " + Note.notes.size() + " open notes!");
			}
		}
	}
	
	@Override
	public boolean onDisable() {
		List<Serializable> strings = new ArrayList<>();
		for (Note note : Note.notes) {
			strings.add(note.toString());
		}
		try {
			JSONManager.saveList(file, strings);
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Command(hook = "an_create")
	public void createNote(CommandSender sender, String note) {
		Player player = (Player) sender;
		new Note(note, player);
		sender.sendMessage(ChatColor.GREEN + "Note added!");
	}
	
	@Command(hook = "an_del")
	public void delNote(CommandSender sender, int id) {
		if (id < Note.notes.size() && id >= 0 && Note.notes.get(id) != null) {
			Note.notes.remove(id);
			sender.sendMessage(ChatColor.GREEN + "Note " + id + " has been removed!");
		} else {
			sender.sendMessage(ChatColor.RED + "That note does not exist!");
		}
	}
	
	@Command(hook = "an_list")
	public void list(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "AdminNotes:");
		for (Note note : Note.notes) {
			String string = ChatColor.YELLOW + "" + Note.notes.indexOf(note) + ": ";
			string += ChatColor.GREEN + note.getNote();
			string += "\n" + ChatColor.YELLOW + "-" + Bukkit.getPlayer(note.getCreator()).getName() + ", " + ChatColor.GOLD;
			SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm");
			string += format.format(new Date(note.getTimeCreated()));
			sender.sendMessage(string);
		}
	}
}
