package com.redstoner.javautils.blockplacemods.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nemez.cmdmgr.Command;
import com.redstoner.javautils.blockplacemods.Boxed;
import com.redstoner.javautils.blockplacemods.PlayerData;
import com.redstoner.javautils.blockplacemods.util.CommandException;
import org.bukkit.entity.Player;

import java.io.IOException;

public abstract class ModBooleanAbstract extends ModAbstract<Boxed<Boolean>> {
	
	{
		cmdManagerString.append("[string:enabled] {");;
		cmdManagerString.append("  run set enabled;");
		cmdManagerString.append("}");
		cmdManagerString.append("{");
		cmdManagerString.append("  run toggle;");
		cmdManagerString.append("}");
	}
	
	private final boolean enabledByDefault = enabledByDefault();
	
	protected abstract boolean enabledByDefault();
	
	@Override
	public Boxed<Boolean> createDefaultData(PlayerData data) {
		return Boxed.box(data, enabledByDefault);
	}
	
	@Override
	public Boxed<Boolean> loadDataFor(PlayerData data, JsonReader reader) throws IOException {
		return Boxed.box(data, reader.nextBoolean());
	}
	
	@Override
	public void saveDataTo(JsonWriter writer, Boxed<Boolean> data) throws IOException {
		writer.value(data.value);
	}
	
	protected boolean hasEnabled(Player player) {
		Boxed<Boolean> data = get(player);
		if (data == null) return enabledByDefault;
		return data.value;
	}
	
	@Command(hook = "toggle")
	protected void onToggle(Player sender) {
		handleMessage(sender, () -> handleToggle(sender));
	}
	
	private String handleToggle(Player sender) throws CommandException {
		Boxed<Boolean> current = getAndEnsureSaved(sender);
		current.value ^= true;
		current.scheduleSave();
		return current.value ? "Enabled" : "Disabled";
	}
	
	@Command(hook = "set")
	protected void onSet(Player sender, String arg) {
		handleMessage(sender, () -> handleSet(sender, arg));
	}
	
	private String handleSet(Player sender, String arg) throws CommandException {
		Boxed<Boolean> current = get(sender);
		
		final boolean enable;
		if (arg == null) {
			throw new CommandException("Missing argument");
		} else {
			switch (arg.toLowerCase()) {
				case "on":
				case "enable":
				case "true":
					enable = true;
					break;
				case "off":
				case "disable":
				case "false":
					enable = false;
					break;
				default:
					throw new CommandException("Input '" + arg + "' was not understood. " + "Use one of: \non, enable, true, off, disable false.");
			}
			if (enable == current.value) {
				return "Was already " + (enable ? "enabled" : "disabled");
			}
		}
		
		current = getAndEnsureSaved(sender);
		current.value = enable;
		current.scheduleSave();
		return current.value ? "Enabled" : "Disabled";
	}
}
