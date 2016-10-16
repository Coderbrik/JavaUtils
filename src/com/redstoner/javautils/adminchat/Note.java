package com.redstoner.javautils.adminchat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Note {
	
	public static List<Note> notes = new ArrayList<>();
	
	private final String note;
	private final UUID creator;
	private final long timestamp;
	
	public Note(String note, Player creator) {
		this.note = note;
		this.creator = creator.getUniqueId();
		timestamp = Calendar.getInstance().getTime().getTime();
		notes.add(this);
	}
	
	private Note(String note, UUID creator, long timestamp) {
		this.note = note;
		this.creator = creator;
		this.timestamp = timestamp;
	}
	
	public long getTimeCreated() {
		return timestamp;
	}
	
	public String getNote() {
		return note;
	}
	
	public UUID getCreator() {
		return creator;
	}
	
	public String toString() {
		return creator.toString() + ";" + note + ";" + timestamp;
	}
	
	public static Note fromString(String string) {
		String[] split = string.split(";");
		UUID creator = UUID.fromString(split[0]);
		String note = split[1];
		long timestamp = Long.parseLong(split[2]);
		return new Note(note, creator, timestamp);
	}
	
}
