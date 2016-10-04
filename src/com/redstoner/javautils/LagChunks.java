package com.redstoner.javautils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nemez.cmdmgr.Command;
import com.redstoner.moduleLoader.Module;

public class LagChunks extends Module {
	private List<LaggyChunk> laggyChunks = new ArrayList<LaggyChunk>();

	@Override
	public String getName() {
		return "LagChunks";
	}

	@Override
	public String getDescription() {
		return "Checks for laggy chunks";
	}
	
	private void scan(int amount) {
		laggyChunks.clear();
		
		for (World world : Bukkit.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				if (chunk.getEntities().length > amount) {
					Location entLoc = chunk.getEntities()[0].getLocation();
					
					laggyChunks.add(new LaggyChunk(entLoc.getBlockX(), entLoc.getBlockY(), entLoc.getBlockZ(), world, chunk.getEntities().length));
				}
			}
		}
	}
	
	@Command(hook="list")
	private void list(CommandSender sender) {
		sender.sendMessage("\n§2--=[ LagChunks ]=--");
		
		for (LaggyChunk lc : laggyChunks) {
			sender.sendMessage("§b[§a"+ laggyChunks.indexOf(lc) + "§b]: §a" + lc.x + "§7, §a" + lc.y + "§7, §a" + lc.z + "§7(" + lc.world.getName() + ") §a- §b" + lc.amount + " entities");
		}
		
		sender.sendMessage("§2-------------------");
	}
	
	@Command(hook="scan_cmd")
	public void scan_cmd(CommandSender sender, int amount) {
		scan(amount);
		list(sender);
	}
	
	@Command(hook="tp")
	public void tp(CommandSender sender, int number) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (number < laggyChunks.size()) {
				player.teleport(laggyChunks.get(number).getLocation());
				player.sendMessage("§aSuccesfully teleported to laggy chunk no. " + number);
				
			} else {
				player.sendMessage("§4Invalid chunk number! Use §e/lc list §4 to show laggy chunks!");
			}
		} else {
			sender.sendMessage("§4Only players can teleport to laggy chunks!");
		}
	}

	@Override
	public String getCmdManagerString() {
		return
			"command lc {"
				+ "[int:amount] {"
					+ "run scan_cmd amount;"
					+ "help scans for laggy chunks;"
				+ "}"
				
				+ "list {"
					+ "run list;"
					+ "help re-lists already scanned chunks;"
				+ "}"
				
				+ "tp [int:number] {"
					+ "run tp number;"
					+ "help teleports to the specified chunk;"
				+ "}"
			+ "}";
	}
	
	
}
