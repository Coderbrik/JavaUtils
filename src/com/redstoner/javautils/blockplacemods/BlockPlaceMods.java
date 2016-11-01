package com.redstoner.javautils.blockplacemods;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.redstoner.javautils.blockplacemods.mods.ModAbstract;
import com.redstoner.javautils.blockplacemods.saving.JsonFileAdapter;
import com.redstoner.javautils.blockplacemods.saving.JsonLoadable;
import com.redstoner.javautils.blockplacemods.saving.JsonLoadableAdapter;
import com.redstoner.moduleLoader.Module;
import com.redstoner.moduleLoader.ModuleLoader;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;

public final class BlockPlaceMods extends Module implements Listener {

    private final Map<Player, PlayerData> settings;
    private final File dataFolder;
    private final JsonFileAdapter<PlayerData> fileAdapter;

    public BlockPlaceMods() {
        ModAbstract.constructAll();
        settings = ModAbstract.getSettings();
        dataFolder = new File(ModuleLoader.getLoader().getDataFolder(), getName());

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(JsonLoadable.class, new JsonLoadableAdapter(PlayerData::new))
                .create();

        Consumer<Throwable> onErrorLoad = t -> {
            String trace = ExceptionUtils.getStackTrace(t);
            ModuleLoader.getPlugin().getLogger().severe("[BlockPlaceMods] Failed to load player file: \n" + trace);
        };

        Consumer<Throwable> onErrorSave = t -> {
            String trace = ExceptionUtils.getStackTrace(t);
            ModuleLoader.getPlugin().getLogger().severe("[BlockPlaceMods] Failed to save player file: \n" + trace);
        };

        fileAdapter = new JsonFileAdapter<>(new TypeToken<PlayerData>() {
        }, gson, onErrorLoad, onErrorSave);

        Bukkit.getOnlinePlayers().forEach(this::load);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(ModuleLoader.getLoader(), this::saveScheduledData, 100, 100);

    }

    @Override
    public String getName() {
        return "BlockPlaceMods";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        load(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        save(event.getPlayer(), true);
    }

    private void load(Player player) {
        PlayerData present = settings.get(player);
        if (present != null) {
            save(player, false);
        }

        PlayerData data = fileAdapter.load(getFile(player));
        if (data == null) {
            data = new PlayerData(player);
        }
        settings.put(player, data);
    }

    private boolean save(Player player, boolean unload) {
        PlayerData data = unload ? settings.remove(player) : settings.get(player);
        if (data != null) {
            save(player, data);
            return true;
        }
        return false;
    }

    private void save(Player player, PlayerData data) {
        fileAdapter.save(data, getFile(player));
    }

    private File getFile(Player player) {
        return new File(dataFolder, player.getUniqueId().toString() + ".json");
    }

    private void saveScheduledData() {
        settings.forEach((player, data) -> {
            if (data.isSaveScheduled()) {
                save(player, data);
            }
        });
    }

}
