package com.redstoner.javautils.loginSecurity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;

public class LoginSecurity extends Module implements Listener {
	private List<Player> loggingIn;
	
	@Override
	public String getName() {
		return "LoginSecurity";
	}
	
	@Override
	public String getDescription() {
		return "Allows you to protect your account with a password";
	}
	
	@Override
	public void onEnable() {
		loggingIn = new ArrayList<>();
	}
	
	@Command(hook = "register")
	public void register(CommandSender sender, String password) {
		
	}
	
	@Command(hook = "login")
	public void login(CommandSender sender, String password) {
		
	}
	
	@Command(hook = "cgpass")
	public void cgpass(CommandSender sender, String oldPassword, String newPassword) {
		
	}
	
	@Command(hook = "rmpass")
	public void rmpass(CommandSender sender) {
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
		loggingIn.add(player);
		
		Thread playerLoginThread = new Thread() {
			@Override
			public void run() {
				Long endTime = System.currentTimeMillis() + 60000;
				
				while (endTime > System.currentTimeMillis()) {
					if (!player.isOnline()) {
						loggingIn.remove(player);
					}
					
					//this is seperate to check for logins
					if (!loggingIn.contains(player)) {
						player.sendMessage(ChatColor.GREEN + "Successfully logged in!");
						break;
					}
				}
				
				if (loggingIn.contains(player)) {
					player.kickPlayer("You didn't login in time!");
				}
			}
		};
		
		playerLoginThread.start();
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
	}
	
	//@formatter:off - turns off formatter (has to be a comment)
	@Override
	public String getCmdManagerString() {
		return
			"command register [string:password] {"
				+ "run register password;"
				+ "help Protects your account with a password;"
				+ "type player;"
			+ "}"
			
			+ "command login [string:password] {"
				+ "run login password;"
				+ "help Logs you in, allowing you to access your account;"
				+ "type player;"
			+ "}"
			
			+ "command cgpass [string:oldPassword] [string:newPassword] {"
				+ "run cgpass oldPassword newPassword;"
				+ "help Changes your password to the specified one;"
				+ "type player;"
			+ "}"
			
			+ "command rmpass {"
				+ "run rmpass;"
				+ "help Removes your account's password;"
				+ "type player;"
			+ "}";
	}
	//@formatter:on - turns on formatter (has to be a comment)
	
}
