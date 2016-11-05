package com.redstoner.javautils.nametags;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import com.redstoner.moduleLoader.Module;

public class NameTags extends Module implements Listener {
	private Scoreboard scoreboard = null;
	
	@Override
	public String getName() {
		return "NameTags";
	}
	
	@Override
	public String getDescription() {
		return "Uses scoreboards to make tab look nice";
	}
	
	@Override
	public void onEnable() {
		scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		
		for (Rank rank : Rank.values()) {
			if (scoreboard.getTeam(rank.getScoreboardName()) == null) {
				scoreboard.registerNewTeam(rank.getScoreboardName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard teams option " + rank.getScoreboardName() + " color " + rank.getColor());
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		String playerName = e.getPlayer().getName();
		Rank rank = Rank.VISITOR;
		
		for (Rank r : Rank.values()) {
			if (e.getPlayer().hasPermission(r.getPermission())) {
				if (r.getPriority() > rank.getPriority()) {
					rank = r;
				}
			}
		}
		
		for (Rank r : Rank.values()) {
			Team team = scoreboard.getTeam(r.getScoreboardName());
			
			if (team.hasEntry(playerName)) {
				team.removeEntry(playerName);
			}
		}
		
		scoreboard.getTeam(rank.getScoreboardName()).addEntry(playerName);
	}
}
