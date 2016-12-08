package com.redstoner.javautils.blockplacemods.mods;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.CommandManager;
import com.redstoner.javautils.blockplacemods.DefaultingMap;
import com.redstoner.javautils.blockplacemods.PlayerData;
import com.redstoner.javautils.blockplacemods.util.CommandException;
import com.redstoner.javautils.blockplacemods.util.ThrowingSupplier;
import com.redstoner.moduleLoader.misc.BukkitPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ModAbstract<T> implements Mod<T>, Listener {
	
	private static final DefaultingMap<Player, PlayerData>	settings	= new DefaultingMap<>(PlayerData::new);
	private static final Set<Mod>							mods		= new HashSet<>();
	
	public static Map<Player, PlayerData> getSettings() {
		return settings;
	}
	
	public static Set<Mod> getMods() {
		return mods;
	}
	
	public static Mod getMod(String name) {
		for (Mod mod : ModAbstract.getMods()) {
			if (name.equals(mod.getName())) {
				return mod;
			}
		}
		return null;
	}
	
	public static void constructAll() {
		new ModBooleanCauldron();
		new ModBooleanPiston();
		new ModBooleanStep();
		new ModBooleanTorch();
		new ModInventoryDropper();
		new ModInventoryFurnace();
		new ModInventoryHopper();
	}
	
	private final Set<String>		aliases;
	private final T					defaultValue;
	protected final StringBuilder	cmdManagerString;
	
	{
		aliases = new HashSet<>();
		aliases.add(getName());
		defaultValue = createDefaultData(null);
		cmdManagerString = new StringBuilder();
		cmdManagerString.append("help {");
		cmdManagerString.append("  run help;");
		cmdManagerString.append("}  ");
		cmdManagerString.append("info {");
		cmdManagerString.append("  run help;");
		cmdManagerString.append("}  ");
		cmdManagerString.append("  ");
		
		CommandManager.registerCommand(getCmdManagerString(), this, BukkitPlugin.INSTANCE);
		Bukkit.getPluginManager().registerEvents(this, BukkitPlugin.INSTANCE);
		mods.add(this);
	}
	
	@Override
	public Set<String> getAliases() {
		return aliases;
	}
	
	@Override
	public T defaultData(PlayerData data) {
		if (data == null || data.isDefault()) {
			return defaultValue;
		}
		return createDefaultData(data);
	}
	
	protected abstract T createDefaultData(PlayerData entryFor);
	
	protected T get(Player player) {
		return settings.get(player).get(this);
	}
	
	protected boolean reset(Player player) {
		return settings.get(player).get().remove(this) != null;
	}
	
	protected boolean present(Player player) {
		return settings.get(player).get().containsKey(this);
	}
	
	protected T getAndEnsureSaved(Player player) {
		return settings.getAndEnsurePresent(player).getEnsureSaved(this);
	}
	
	@Command(hook = "help")
	protected void onCommandHelp(CommandSender sender) {
		sender.sendMessage(getDetailedDescription());
	}
	
	private String getCmdManagerString() {
		return String.format("command mod $name$ { perm utils.$name$; type player; %s }", cmdManagerString.toString()).replaceAll("$name$", getName().toLowerCase());
	}
	
	protected void handleMessage(Player sender, ThrowingSupplier<String> supplier) {
		String message;
		try {
			message = supplier.get();
		} catch (CommandException e) {
			message = "&c" + e.getMessage();
		} catch (Throwable t) {
			message = "&cAn unexpected error occurred while executing this command.";
			t.printStackTrace();
		}
		
		message = "&a[&cBPM&a][&c" + getName() + "&a]" + message;
		message = ChatColor.translateAlternateColorCodes('&', message);
		sender.sendMessage(message);
	}
	
}
