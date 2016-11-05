package com.redstoner.javautils.blockplacemods.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.redstoner.javautils.blockplacemods.PlayerData;

import java.io.IOException;
import java.util.Set;

public interface Mod<T> {
	
	String getName();
	
	String getSimpleDescription();
	
	String getDetailedDescription();
	
	Set<String> getAliases();
	
	T defaultData(PlayerData data);
	
	T loadDataFor(PlayerData data, JsonReader reader) throws IOException;
	
	void saveDataTo(JsonWriter writer, T data) throws IOException;
}
