package com.redstoner.javautils.imout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nemez.cmdmgr.Command;
import com.redstoner.javautils.adminchat.AdminChat;
import com.redstoner.moduleLoader.Module;

public class Imout extends Module{

	List<String> imout_toggle_list = new ArrayList<String>();
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Adds /imout, displays fake leave/join messages";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Imout";
	}
	
	@Command(hook = "imout")
	public void on_imout_command(CommandSender sender, String not){
		if (!(sender instanceof Player)){
			sender.sendMessage("§cThis command can't be run from the console");
		}
		
		else{
			Player s = (Player) sender;
			String name = sender.getName();
			if(imout_toggle_list.contains(name)){
				 String symbol = "§a§l+";
				 sender.sendMessage("§eWelcome back! You are no longer hidden");
				 sender.sendMessage("§6We disabled /act for you!");
				 s.performCommand("vanish off");
				 imout_toggle_list.remove(name);
				 Bukkit.broadcastMessage(symbol + " §7" + name);
				 if(AdminChat.ac_toggle_list.contains(name)){
					 AdminChat.ac_toggle_list.remove(name);
				 }
			}
			
			else{
				String symbol = "§c§l-";
				sender.sendMessage("§eYou just left... Or didn't you?");
				imout_toggle_list.add(name);
				s.sendMessage("§6We enabled /act for you!");
				s.performCommand("vanish on");
				if(!(AdminChat.ac_toggle_list.contains(name))){
					 AdminChat.ac_toggle_list.add(name);
				 }
				Bukkit.broadcastMessage(symbol + " §7" + name);
			}
		}
	}
}