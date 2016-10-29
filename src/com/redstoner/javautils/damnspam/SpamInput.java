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
		string = string.substring(1,string.length() - 1);
		String[] args = string.split(",");
		Player player = null;
		double timeoutOff = 0;
		double timeoutOn = 0;
		double lastTime = 0;
		for(String s : args){
			String[] vals = s.split(":");
			String name = vals[0].replace(" ", "").replace("\"", "");
			String val = vals[1].replace(" ", "").replace("\"", "");
			switch (name){
			case "creator":
				player = Bukkit.getPlayer(UUID.fromString(val));
				break;
			case "timeout_on":
				timeoutOn = Double.parseDouble(val);
				break;
			case "timeout_off":
				timeoutOff = Double.parseDouble(val);
				break;
			case "last_time":
				lastTime = Double.parseDouble(val);
				break;
			}
		}
		return new SpamInput(player,timeoutOff,timeoutOn,lastTime);
	}
	
	@Override
	public String toString(){
		return "{\"creator\": \"" + player.getUniqueId().toString() + "\", \"timeout_on\": " + timeoutOn + ", \"timeout_off\": " + timeoutOn + ", \"last_time\": " + lastTime + "}";
	}
	
}
