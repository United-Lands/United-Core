package org.unitedlands.items;

import org.bukkit.plugin.java.JavaPlugin;

public class UnitedItems extends JavaPlugin {

    private ItemDetector itemDetector;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        itemDetector = new ItemDetector(this);
        getServer().getPluginManager().registerEvents(itemDetector, this);
    }

    @Override
    public void onDisable() {
        if (itemDetector != null) {
            itemDetector.saveData();
        }
    }
}
