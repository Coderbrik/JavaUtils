package com.redstoner.javautils.blockplacemods.util;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.redstoner.javautils.blockplacemods.saving.JsonLoadable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemProperties implements JsonLoadable {

    private int id;
    private byte data;
    private int amount;
    private Map<String, Integer> enchantments;
    private List<String> lore;
    private String displayName;
    private boolean unbreakable;

    public ItemProperties() {
    }

    public ItemProperties(ItemStack stack) {
        id = stack.getTypeId();
        data = stack.getData().getData();
        amount = stack.getAmount();
        enchantments = new HashMap<>();

        ItemMeta meta = new DynamicEnchantmentMeta(stack.getItemMeta());
        if (meta.hasEnchants()) {
            enchantments = new HashMap<>();
            meta.getEnchants().forEach((ench, level) -> {
                enchantments.put(ench.getName(), level);
            });
        }

        if (meta.hasLore()) {
            lore = meta.getLore();
        }

        if (meta.hasDisplayName()) {
            displayName = meta.getDisplayName();
        }

        unbreakable = meta.spigot().isUnbreakable();
    }

    public ItemStack toItemStack() {
        ItemStack result = new ItemStack(id, amount, data);

        DynamicEnchantmentMeta meta = new DynamicEnchantmentMeta(result.getItemMeta());
        if (enchantments != null) {
            enchantments.forEach((name, level) -> {
                Enchantment ench = Enchantment.getByName(name);
                if (ench != null) {
                    meta.addEnchant(ench, level, true);
                }
            });
        }

        if (lore != null) {
            meta.setLore(lore);
        }

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }

        meta.spigot().setUnbreakable(unbreakable);

        result.setItemMeta(meta.getActualMeta());
        return result;
    }

    @Override
    public void writeTo(JsonWriter writer) throws IOException {
        writer.beginObject();

        writer.name("id").value(id);
        writer.name("data").value(data);
        writer.name("amount").value(amount);

        if (displayName != null) {
            writer.name("displayName").value(displayName);
        }

        if (enchantments != null) {
            writer.name("enchantments");
            JsonUtil.insert(writer, enchantments);
        }

        if (lore != null) {
            writer.name("lore");
            JsonUtil.insert(writer, lore);
        }

        if (unbreakable) {
            writer.name("unbreakable").value(true);
        }

        writer.endObject();
    }

    @Override
    public void loadFrom(JsonReader reader) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            final String key = reader.nextName();
            switch (key) {
                case "id":
                    id = reader.nextInt();
                    break;
                case "data":
                    data = (byte) reader.nextInt();
                    break;
                case "amount":
                    amount = reader.nextInt();
                    break;
                case "unbreakable":
                    unbreakable = reader.nextBoolean();
                    break;
                case "enchantments":
                    enchantments = (Map<String, Integer>) JsonUtil.read(reader);
                    break;
                case "lore":
                    lore = (List<String>) JsonUtil.read(reader);
                    break;
                case "displayName":
                    displayName = reader.nextString();
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
    }

    public int getId() {
        return id;
    }

    public byte getData() {
        return data;
    }

    public int getAmount() {
        return amount;
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }
}
