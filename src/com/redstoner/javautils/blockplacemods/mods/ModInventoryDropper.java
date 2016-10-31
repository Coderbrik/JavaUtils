package com.redstoner.javautils.blockplacemods.mods;

import org.bukkit.event.inventory.InventoryType;

public class ModInventoryDropper extends ModInventoryAbstract {

    @Override
    public String getName() {
        return "Dropper";
    }

    @Override
    public String getSimpleDescription() {
        return null;
    }

    @Override
    public String getDetailedDescription() {
        return null;
    }

    @Override
    protected InventoryType getInventoryType() {
        return InventoryType.DROPPER;
    }
}
