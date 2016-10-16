package com.redstoner.javautils.loginSecurity;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;
import com.redstoner.moduleLoader.ModuleLoader;
import com.redstoner.moduleLoader.mysql.elements.ConstraintOperator;
import com.redstoner.moduleLoader.mysql.elements.MysqlConstraint;
import com.redstoner.moduleLoader.mysql.elements.MysqlDatabase;
import com.redstoner.moduleLoader.mysql.elements.MysqlField;
import com.redstoner.moduleLoader.mysql.elements.MysqlTable;
import com.redstoner.moduleLoader.mysql.types.text.VarChar;

public class LoginSecurity extends Module implements Listener {
	private List<UUID>	loggingIn;
	private MysqlTable	table;
	
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
		ModuleLoader loader = ModuleLoader.getLoader();
		
		Map<String, String> config = loader.getConfiguration("LoginSecurity.json");
		
		if (config == null || !config.containsKey("database") || !config.containsKey("table")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not load the LoginSecurity config file, disabling!");
			
			enabled = false;
			return;
		}
		
		try {
			MysqlDatabase database = ModuleLoader.getLoader().getMysqlHandler().getDatabase(config.get("database"));
			
			MysqlField uuid = new MysqlField("uuid", new VarChar(36), true);
			MysqlField pass = new MysqlField("pass", new VarChar(38), true);
			
			database.createTableIfNotExists(config.get("table"), uuid, pass);
			
			table = database.getTable(config.get("table"));
		} catch (NullPointerException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not use the LoginSecurity config, disabling!");
			
			enabled = false;
			return;
		}
		
		loggingIn = new ArrayList<>();
	}
	
	@Command(hook = "register")
	public void register(CommandSender sender, String password) {
		Player player = (Player) sender;
		
		if (isRegistered(player)) {
			player.sendMessage(ChatColor.GREEN + "You are already registered!");
			return;
		}
		
		try {
			if (registerPlayer(player, password)) {
				player.sendMessage(ChatColor.GREEN + "Succesfully registered!");
			}
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		
		player.sendMessage(ChatColor.RED + "Failed to register, please contact an admin!");
	}
	
	@Command(hook = "login")
	public void login(CommandSender sender, String password) {
		Player player = (Player) sender;
		
		if (!isRegistered(player)) {
			player.sendMessage(ChatColor.RED + "You are not registered!");
			return;
		}
		
		if (CryptographyHandler.verify(password, getHash(player))) {
			loggingIn.remove(player.getUniqueId());
			player.sendMessage(ChatColor.GREEN + "Successfully logged in!");
		} else {
			player.sendMessage(ChatColor.RED + "Wrong password!");
		}
	}
	
	@Command(hook = "cgpass")
	public void cgpass(CommandSender sender, String oldPassword, String newPassword) {
		Player player = (Player) sender;
		
		if (loggingIn.contains(player.getUniqueId())) {
			player.sendMessage("You have to log in first!");
			return;
		}
		
		if (!isRegistered(player)) {
			player.sendMessage(ChatColor.RED + "You are not registered!");
			return;
		}
		
		if (!CryptographyHandler.verify(oldPassword, getHash(player))) {
			player.sendMessage(ChatColor.RED + "The old password you entered is wrong!");
			return;
		}
		
		if (oldPassword.equals(newPassword)) {
			player.sendMessage(ChatColor.RED + "You entered the same password!");
			return;
		}
		
		if (table.delete(getUuidConstraint(player))) {
			try {
				registerPlayer(player, newPassword);
				player.sendMessage(ChatColor.GREEN + "Succesfully changed password!");
				
			} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
				e.printStackTrace();
				player.sendMessage(ChatColor.RED + "Failed to set new password!");
			}
		} else {
			player.sendMessage(ChatColor.RED + "Failed to remove old password from database!");
		}
	}
	
	@Command(hook = "rmpass")
	public void rmpass(CommandSender sender, String oldPassword) {
		Player player = (Player) sender;
		
		if (loggingIn.contains(player.getUniqueId())) {
			player.sendMessage("You have to log in first!");
			return;
		}
		
		if (!isRegistered(player)) {
			player.sendMessage(ChatColor.RED + "You are not registered!");
			return;
		}
		
		if (!CryptographyHandler.verify(oldPassword, getHash(player))) {
			player.sendMessage(ChatColor.RED + "The old password you entered is wrong!");
			return;
		}
		
		if (table.delete(getUuidConstraint(player))) {
			player.sendMessage(ChatColor.GREEN + "Succesfully removed password!");
		} else {
			player.sendMessage(ChatColor.RED + "Failed to remove old password from database!");
		}
	}
	
	@Command(hook = "rmotherpass")
	public void rmotherpass(CommandSender sender, String playerName) {
		if (playerName.equals("")) {
			sender.sendMessage(ChatColor.RED + "That's not a valid player!");
			return;
		}
		
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		
		if (!isRegistered((Player) player)) {
			sender.sendMessage(ChatColor.RED + "That player is not registered!");
			return;
		}
		
		if (table.delete(getUuidConstraint((Player) player))) {
			sender.sendMessage(ChatColor.GREEN + "Successfully removed " + playerName + "'s password!");
		} else {
			sender.sendMessage(ChatColor.RED + "Failed to remove " + playerName + "'s password!");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
		if (!isRegistered(player)) {
			return;
		}
		
		loggingIn.add(player.getUniqueId());
		
		Thread playerLoginThread = new Thread() {
			@Override
			public void run() {
				Long endTime = System.currentTimeMillis() + 60000;
				
				while (endTime > System.currentTimeMillis()) {
					if (!player.isOnline()) {
						loggingIn.remove(player.getUniqueId());
					}
					
					// this is seperate to check for logins
					if (!loggingIn.contains(player.getUniqueId())) {
						player.sendMessage(ChatColor.GREEN + "Successfully logged in!");
						break;
					}
				}
				
				if (loggingIn.contains(player.getUniqueId())) {
					player.kickPlayer("You didn't login in time!");
				}
			}
		};
		
		playerLoginThread.start();
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
	}
	
	public MysqlConstraint getUuidConstraint(Player player) {
		return new MysqlConstraint("uuid", ConstraintOperator.EQUAL, player.getUniqueId().toString());
	}
	
	public boolean isRegistered(Player player) {
		return table.get("uuid", getUuidConstraint(player)).length > 0;
	}
	
	public String getHash(Player player) {
		return (String) table.get("pass", getUuidConstraint(player))[0];
	}
	
	public boolean registerPlayer(Player player, String password)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		String salt = CryptographyHandler.generateSalt();
		String hash = CryptographyHandler.hash(password, salt);
		
		String toInsert = "$pbkdf2-sha256$200000$" + salt + "$" + hash;
		
		return table.insert(player.getUniqueId().toString(), toInsert);
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
			
			+ "command rmpass [string:oldPassword] {"
				+ "run rmpass oldPassword;"
				+ "help Removes your account's password;"
				+ "type player;"
			+ "}"
			
			+ "command rmotherpass [string:playerName] {"
				+ "run rmotherpass playerName;"
				+ "help removes another player's password;"
				+ "perm utils.loginsecurity.admin;"
			+ "}";
	}
	//@formatter:on - turns on formatter (has to be a comment)
	
}
