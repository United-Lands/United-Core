package org.unitedlands.items;

import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.items.util.GenericLocation;
import org.unitedlands.items.util.SerializableData;

import java.util.Map;

public class UnitedItems extends JavaPlugin {

    private ItemDetector itemDetector;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize and register the ItemDetector
        itemDetector = new ItemDetector(this);
        getServer().getPluginManager().registerEvents(itemDetector, this);

        // Load saplings
        itemDetector.loadSaplings();
    }

    @Override
    public void onDisable() {
        if (itemDetector != null) {
            Map<GenericLocation, String> saplingData = itemDetector.getSerializableSaplings();
            SerializableData.Farming.writeToDatabase(saplingData, "sapling.dat");
        }
    }

}
