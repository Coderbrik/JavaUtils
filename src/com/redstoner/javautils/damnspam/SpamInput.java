package com.redstoner.javautils.damnspam;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpamInput {

	protected Player player;
	protected double timeoutOff;
	protected double timeoutOn;
	protected double lastTime;
	
	protected SpamInput(Player player, double timeoutOff, double timeoutOn, double lastTime) {
		this.player = player;
		this.timeoutOff = timeoutOff;
		this.timeoutOn = timeoutOn;
		this.lastTime = lastTime;
	}
	
	protected static SpamInput fromString(String string){
		String[] args = string.split(";");
		return new SpamInput(Bukkit.getPlayer(UUID.fromString(args[0])),Double.parseDouble(args[1]),Double.parseDouble(args[2]),Double.parseDouble(args[3]));
	}
	
	@Override
	public String toString(){
		return player.getUniqueId().toString() + ";" + timeoutOff + ";" + timeoutOn + ";" + lastTime;
	}
	
}
