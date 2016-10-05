package com.redstoner.javautils.busy;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;
import com.redstoner.moduleLoader.ModuleLoader;

public class Busy extends Module implements Listener {
	
	private List<Player> busy = new ArrayList<>();
	private Map<Player, String> targets = new HashMap<>();
	private String[] aliases = {"emsg", "msg", "tell", "etell", "w", "ew", "t", "et", "pm", "epm"};
	private String[] replyAliases = {"r", "er", "reply", "ereply"};
	
	@Override
	public void onEnable() {
		try {
			File file = new File(Busy.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			JarFile jar = new JarFile(file);
			ModuleLoader.registerCommands(jar.getInputStream(jar.getEntry("com/redstoner/javautils/busy/commands.txt")), this);
			jar.close();
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Command(hook = "busy_on")
	public void busyOn(CommandSender sender) {
		Player player = (Player) sender;
		if (busy.contains(player)) {
			player.sendMessage(ChatColor.RED + "You are already busy!");
			return;
		}
		busy.add(player);
		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RESET + "" + ChatColor.GRAY + " is now busy");
	}
	
	@Command(hook = "busy_off")
	public void busyOff(CommandSender sender) {
		Player player = (Player) sender;
		if (!busy.contains(player)) {
			player.sendMessage(ChatColor.RED + "You aren't currently busy!");
			return;
		}
		busy.remove(player);
		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RESET + "" + ChatColor.GRAY + " is no longer busy");
	}
	
	@EventHandler
	public void onCommandSend(PlayerCommandPreprocessEvent e) {
		String command = e.getMessage().replaceAll("^/", "");
		String[] args = command.split(" ");
		String cmd = args[0];
		boolean message = false;
		for (String alias : aliases) {
			if (alias.equalsIgnoreCase(cmd)) {
				message = true;
			}
		}
		targets.put(e.getPlayer(), args[1]);
		for (String alias : replyAliases) {
			if (alias.equalsIgnoreCase(cmd)) {
				for (Player p : busy) {
					if (targets.get(e.getPlayer()) != null && p.getName().contains(targets.get(e.getPlayer()))) {
						e.getPlayer().sendMessage(ChatColor.RED + "You may not message " + p.getName() + " at this time, they are busy.");
						e.setCancelled(true);
						return;
					}
				}
				return;
			}
		}
		if (!message || e.getPlayer().hasPermission("utils.imbusy.override")) {
			return;
		}
		for (Player p : busy) {
			if (p.getName().contains(args[1])) {
				e.getPlayer().sendMessage(ChatColor.RED + "You may not message " + p.getName() + " at this time, they are busy.");
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@Override
	public void onDisable() {
	}

	@Override
	public String getName() {
		return "Busy";
	}

	@Override
	public String getDescription() {
		return "Allows you to set a busy state, which can disable private messages";
	}
	
	
	
}
