package com.redstoner.javautils;

import org.bukkit.Location;
import org.bukkit.World;

public class LaggyChunk {
	public final double x, y, z;
	public final int amount;
	public final World world;
	
	public LaggyChunk(double x, double y, double z, World world, int amount) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.amount = amount;
	}
	
	public Location getLocation() {
		return new Location(world, x, y, z);
	}
}
