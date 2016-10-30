package com.redstoner.javautils.damnspam;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class SpamInput {

	@SerializedName("creator")
	private UUID player;
	
	@SerializedName("timeout_on")
	private double timeoutOn;
	
	@SerializedName("timeout_off")
	private double timeoutOff;
	
	@SerializedName("last_time")
	private double lastTime;

	protected SpamInput(Player player, double timeoutOff, double timeoutOn, double lastTime) {
		this.player = player.getUniqueId();
		this.timeoutOff = timeoutOff;
		this.timeoutOn = timeoutOn;
		this.lastTime = lastTime;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}

	public double getTimeoutOn() {
		return timeoutOn;
	}

	public double getTimeoutOff() {
		return timeoutOff;
	}

	public double getLastTime() {
		return lastTime;
	}

	public void setPlayer(UUID player) {
		this.player = player;
	}

	public void setTimeoutOn(double timeoutOn) {
		this.timeoutOn = timeoutOn;
	}

	public void setTimeoutOff(double timeoutOff) {
		this.timeoutOff = timeoutOff;
	}

	public void setLastTime(double lastTime) {
		this.lastTime = lastTime;
	}

	protected static SpamInput fromString(String string) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(string, SpamInput.class);
	}

	@Override
	public String toString() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}

}
