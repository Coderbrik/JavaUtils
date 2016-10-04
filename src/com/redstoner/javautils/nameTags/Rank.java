package com.redstoner.javautils.nameTags;

import org.bukkit.ChatColor;

public enum Rank {
	VISITOR, MEMBER, BUILDER, TRUSTED, TRAININGMOD, MOD, ADMIN;
	
	public String getScoreboardName() {
		switch (this) {
			case VISITOR:
				return "g_visitor";
			case MEMBER:
				return "f_member";
			case BUILDER:
				return "e_builder";
			case TRUSTED:
				return "d_trusted";
			case TRAININGMOD:
				return "c_trainingmod";
			case MOD:
				return "b_mod";
			case ADMIN:
				return "a_admin";
			default:
				return "g_visitor";
		}
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
			case TRAININGMOD:
				return "group.trainingmod";
			case MOD:
				return "group.mod";
			case ADMIN:
				return "group.admin";
			default:
				return "group.visitor";
		}
	}
	
	public ChatColor getColor() {
		switch (this) {
			case VISITOR:
				return ChatColor.GRAY;
			case MEMBER:
				return ChatColor.WHITE;
			case BUILDER:
				return ChatColor.GREEN;
			case TRUSTED:
				return ChatColor.DARK_AQUA;
			case TRAININGMOD:
				return ChatColor.RED;
			case MOD:
				return ChatColor.RED;
			case ADMIN:
				return ChatColor.DARK_RED;
			default:
				return ChatColor.GRAY;
		}
	}
}
