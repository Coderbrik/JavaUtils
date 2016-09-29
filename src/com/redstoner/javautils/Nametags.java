package com.redstoner.javautils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.redstoner.moduleLoader.Module;

public class Nametags extends Module implements Listener {

	@Override
	public void onEnable() {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		
		for (Rank rank : Rank.values()) {
			if (!(scoreboard.getTeam(rank.getPrefix() + "_" + rank.toString().toLowerCase()) == null)) {
				Team team = scoreboard.registerNewTeam(rank.getPrefix() + "_" + rank.toString().toLowerCase());
				team.setPrefix(rank.getColor());
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		Rank rank = Rank.VISITOR;
		String team;
		
		for (Rank r : Rank.values()) {
			if (player.hasPermission(r.getPermission()) && r.getRank() < rank.getRank()) {
				rank = r;
			}
		}
		team = rank.getPrefix() + "_" + rank.toString().toLowerCase();
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team).addEntry(player.getName());
	}
	
	@Override
	public String getName() {
		return "Nametags";
	}
	
	@Override
	public String getDescription() {
		return "Configures rank and name colors";
	}
	
}
enum Rank {
	
	VISITOR,
	MEMBER,
	BUILDER,
	TRUSTED,
	MIT,
	MOD,
	ADMIN;
	
	public String getPrefix() {
		switch (this) {
			case VISITOR:
				return "g";
			case MEMBER:
				return "f";
			case BUILDER:
				return "e";
			case TRUSTED:
				return "d";
			case MIT:
				return "c";
			case MOD:
				return "b";
			case ADMIN:
				return "a";
		}
		return "";
	}
	
	public String getPermission() {
		switch (this) {
			case VISITOR:
				return "group.visitor";
			case MEMBER:
				return "group.member";
			case BUILDER:
				return "group.builder";
			case TRUSTED:
				return "group.trusted";
			case MIT:
				return "group.mit";
			case MOD:
				return "group.mod";
			case ADMIN:
				return "group.admin";
		}
		return "";
	}
	
	public int getRank() {
		switch (this) {
			case VISITOR:
				return 7;
			case MEMBER:
				return 6;
			case BUILDER:
				return 5;
			case TRUSTED:
				return 4;
			case MIT:
				return 3;
			case MOD:
				return 2;
			case ADMIN:
				return 1;
		}
		return 0;
	}
	
	public String getColor() {
		switch (this) {
			case VISITOR:
				return ChatColor.COLOR_CHAR + "7";
			case MEMBER:
				return ChatColor.COLOR_CHAR + "f";
			case BUILDER:
				return ChatColor.COLOR_CHAR + "a";
			case TRUSTED:
				return ChatColor.COLOR_CHAR + "3";
			case MIT:
				return ChatColor.COLOR_CHAR + "c";
			case MOD:
				return ChatColor.COLOR_CHAR + "c";
			case ADMIN:
				return ChatColor.COLOR_CHAR + "4";
		}
		return "";
	}
	
}