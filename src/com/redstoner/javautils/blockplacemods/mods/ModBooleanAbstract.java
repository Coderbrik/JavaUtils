package com.redstoner.javautils.blockplacemods.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nemez.cmdmgr.Command;
import com.redstoner.javautils.blockplacemods.Boxed;
import com.redstoner.javautils.blockplacemods.PlayerData;
import org.bukkit.entity.Player;

import java.io.IOException;

public abstract class ModBooleanAbstract extends ModAbstract<Boxed<Boolean>> {

    {
        cmdManagerString.append("[string:enabled] {");
        cmdManagerString.append("  type player;");
        cmdManagerString.append("  run toggle enabled;");
        cmdManagerString.append("}");
    }

    protected abstract boolean enabledByDefault();

    @Override
    public Boxed<Boolean> createDefaultData(PlayerData data) {
        return Boxed.box(data, enabledByDefault());
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
        if (data == null) return enabledByDefault();
        return data.value;
    }

    @Command(hook = "toggle")
    protected void onCommand(Player sender, String arg) {
        Boxed<Boolean> current = get(sender);

        final boolean enable;
        if (arg == null) {
            enable = !current.value;
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
                    sender.sendMessage("Input '" + arg + "' was not understood.");
                    return;
            }
            if (enable == current.value) {
                sender.sendMessage(getName() + " mod was already " + (enable ? "enabled" : "disabled"));
                return;
            }
        }

        current = getAndEnsureSaved(sender);
        current.value = enable;
        current.scheduleSave();
    }
}
