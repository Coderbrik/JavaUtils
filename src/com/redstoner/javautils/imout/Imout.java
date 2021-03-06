package com.redstoner.javautils.imout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nemez.cmdmgr.Command;
import com.redstoner.javautils.adminchat.AdminChat;
import com.redstoner.moduleLoader.interfaces.Module;

public class Imout implements Module {
	
	List<String> imout_toggle_list = new ArrayList<String>();
	
	@Override
	public String getDescription() {
		return "Adds /imout, displays fake leave/join messages";
	}
	
	@Override
	public String getName() {
		return "imout";
	}
	
	@Command(hook = "imout")
	public void onImoutCommand(CommandSender sender) {
		Player s = (Player) sender;
		String name = sender.getName();
		if (imout_toggle_list.contains(name)) {
			String symbol = "§a§l+";
			sender.sendMessage("§eWelcome back! You are no longer hidden");
			sender.sendMessage("§6We disabled /act for you!");
			s.performCommand("vanish off");
			if (AdminChat.ac_toggle_list.contains(name)) {
				AdminChat.ac_toggle_list.remove(name);
			}
			imout_toggle_list.remove(name);
			Bukkit.broadcastMessage(symbol + " §7" + name);
		}
			
		else {
			String symbol = "§c§l-";
			sender.sendMessage("§eYou just left... Or didn't you?");
			imout_toggle_list.add(name);
			s.sendMessage("&6We enabled /act for you!");
			s.performCommand("vanish on");
			if (!(AdminChat.ac_toggle_list.contains(name))) {
				AdminChat.ac_toggle_list.add(name);
			}
			Bukkit.broadcastMessage(symbol + " §7" + name);
		}
	}
}
