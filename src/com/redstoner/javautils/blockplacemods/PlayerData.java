package com.redstoner.javautils.blockplacemods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.redstoner.javautils.blockplacemods.mods.Mod;
import com.redstoner.javautils.blockplacemods.saving.JsonLoadable;
import com.redstoner.javautils.blockplacemods.saving.SaveableImpl;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.redstoner.javautils.blockplacemods.mods.ModAbstract.getMod;

public class PlayerData extends SaveableImpl implements JsonLoadable {
	
	private UUID				uuid;
	private Map<Mod, Object>	data;
	
	public UUID getUuid() {
		return uuid;
	}
	
	public Map<Mod, Object> get() {
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Mod mod) {
		Object result = data.get(mod);
		if (result == null) {
			result = mod.defaultData(null);
			if (isDefault()) {
				data.put(mod, result);
			}
		}
		return (T) result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEnsureSaved(Mod mod) {
		Object result = data.get(mod);
		if (result == null) {
			result = mod.defaultData(this);
			data.put(mod, result);
		}
		return (T) result;
	}
	
	public PlayerData() {
		data = new HashMap<>();
	}
	
	public PlayerData(Player player) {
		this();
		if (player != null) {
			uuid = player.getUniqueId();
		}
	}
	
	public boolean isDefault() {
		return uuid == null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void writeTo(JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("uuid").value(uuid.toString());
		writer.beginObject();
		
		for (Map.Entry<Mod, Object> entry : data.entrySet()) {
			writer.name(entry.getKey().getName());
			entry.getKey().saveDataTo(writer, entry.getValue());
		}
		
		writer.endObject();
		writer.endObject();
	}
	
	@Override
	public void loadFrom(JsonReader reader) throws IOException {
		reader.beginObject();
		
		String uuidString = null;
		
		while (reader.hasNext()) {
			final String key = reader.nextName();
			
			if (key.equals("uuid")) {
				uuidString = reader.nextString();
			} else {
				Mod mod = getMod(key);
				if (mod != null) {
					data.put(mod, mod.loadDataFor(this, reader));
				} else {
					reader.skipValue();
				}
			}
			
		}
		
		if (uuidString == null) {
			throw new IllegalArgumentException("uuid is required in serialized player data");
		}
		
		uuid = UUID.fromString(uuidString);
	}
	
}
