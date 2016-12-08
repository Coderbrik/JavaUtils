package com.redstoner.javautils.loginsecurity;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.interfaces.Module;
import com.redstoner.moduleLoader.json.JSONManager;
import com.redstoner.moduleLoader.misc.BukkitPlugin;
import com.redstoner.moduleLoader.mysql.MysqlHandler;
import com.redstoner.moduleLoader.mysql.elements.ConstraintOperator;
import com.redstoner.moduleLoader.mysql.elements.MysqlConstraint;
import com.redstoner.moduleLoader.mysql.elements.MysqlDatabase;
import com.redstoner.moduleLoader.mysql.elements.MysqlField;
import com.redstoner.moduleLoader.mysql.elements.MysqlTable;
import com.redstoner.moduleLoader.mysql.types.text.VarChar;

public class LoginSecurity implements Module, Listener {
	protected static Map<UUID, Location>	loggingIn;
	private MysqlTable						table;
	
	@Override
	public String getName() {
		return "LoginSecurity";
	}
	
	@Override
	public String getDescription() {
		return "Allows you to protect your account with a password";
	}
	
	@Override
	public boolean onEnable() {
		Map<String, String> config = JSONManager.getConfiguration("LoginSecurity.json");
		
		if (config == null || !config.containsKey("database") || !config.containsKey("table")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not load the LoginSecurity config file, disabling!");
			
			return false;
		}
		
		try {
			MysqlDatabase database = MysqlHandler.INSTANCE.getDatabase(config.get("database"));
			
			MysqlField uuid = new MysqlField("uuid", new VarChar(36), true);
			MysqlField pass = new MysqlField("pass", new VarChar(88), true);
			
			database.createTableIfNotExists(config.get("table"), uuid, pass);
			
			table = database.getTable(config.get("table"));
		} catch (NullPointerException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not use the LoginSecurity config, disabling!");
			
			return false;
		}
		
		loggingIn = new HashMap<>();
		
		Bukkit.getServer().getPluginManager().registerEvents(new CancelledEventsHandler(this), BukkitPlugin.INSTANCE);
		
		return true;
	}
	
	public static Map<UUID, Location> getLoggingIn() {
		return loggingIn;
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
				return;
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
		} else {
			player.sendMessage(ChatColor.RED + "Wrong password!");
		}
	}
	
	@Command(hook = "cgpass")
	public void cgpass(CommandSender sender, String oldPassword, String newPassword) {
		Player player = (Player) sender;
		
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
		
		if (!isRegistered(player)) {
			sender.sendMessage(ChatColor.RED + "That player is not registered!");
			return;
		}
		
		if (table.delete(getUuidConstraint(player))) {
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
		
		loggingIn.put(player.getUniqueId(), player.getLocation());
		
		BukkitScheduler scheduler = Bukkit.getScheduler();
		RepeatingLoginRunnable repeatingRunnable = new RepeatingLoginRunnable(this, player);
		
		repeatingRunnable.setId(scheduler.scheduleSyncRepeatingTask(BukkitPlugin.INSTANCE, repeatingRunnable, 0L, 2L));
		
		scheduler.scheduleSyncDelayedTask(BukkitPlugin.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (isLoggingIn(player)) {
					scheduler.cancelTask(repeatingRunnable.getId());
					player.kickPlayer("You didn't login in time!");
				}
			}
		}, 1200L);
	}
	
	public boolean isLoggingIn(Player player) {
		return loggingIn.containsKey(player.getUniqueId());
	}
	
	public MysqlConstraint getUuidConstraint(OfflinePlayer player) {
		return new MysqlConstraint("uuid", ConstraintOperator.EQUAL, player.getUniqueId().toString());
	}
	
	public boolean isRegistered(OfflinePlayer player) {
		return table.get("uuid", getUuidConstraint(player)).length > 0;
	}
	
	public String getHash(OfflinePlayer player) {
		return (String) table.get("pass", getUuidConstraint(player))[0];
	}
	
	public boolean registerPlayer(Player player, String password)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		String salt = CryptographyHandler.generateSalt();
		String hash = CryptographyHandler.hash(password, salt);
		
		String toInsert = "$pbkdf2-sha256$200000$" + salt + "$" + hash;
		
		return table.insert(player.getUniqueId().toString(), toInsert);
	}
}
