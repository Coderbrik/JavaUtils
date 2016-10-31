package com.redstoner.javautils.blockplacemods.mods;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class ModBooleanStep extends ModBooleanAbstract {

    {
        getAliases().add("Slab");
    }

    @Override
    public String getName() {
        return "Step";
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
    protected boolean enabledByDefault() {
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isStep(event.getBlock().getType())
                && !event.getPlayer().isSneaking()
                && hasEnabled(event.getPlayer())) {
            event.getBlock().setData((byte) 8);
        }
    }

    private boolean isStep(Material block) {
        return block == Material.STEP || block == Material.STONE_SLAB2;
    }

}
