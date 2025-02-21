package org.unitedlands.items.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.unitedlands.items.saplings.CustomSapling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataManager {

    private static final String SAPLING_FILE = "sapling.dat";
    private final Map<Location, CustomSapling> saplingMap = new HashMap<>();

    /*

    #####################################################
    # +-----------------------------------------------+ #
    # |                Data Management                | #
    # +-----------------------------------------------+ #
    #####################################################

    */

    // Load saplings from storage
    @SuppressWarnings("unchecked")
    public void loadSaplings(Map<String, CustomSapling> saplingSets) {
        Map<GenericLocation, String> loadedSaplings = SerializableData.Farming.readFromDatabase(SAPLING_FILE, HashMap.class);
        if (loadedSaplings == null || loadedSaplings.isEmpty()) {
            log("&aNo cached saplings found.");
            return;
        }

        for (Map.Entry<GenericLocation, String> entry : loadedSaplings.entrySet()) {
            GenericLocation genericLocation = entry.getKey();
            Location location = genericLocation.getLocation();
            String saplingId = entry.getValue().toLowerCase(); // Convert to lowercase

            CustomSapling sapling = saplingSets.get(saplingId);
            saplingMap.put(location, sapling);

            Bukkit.getScheduler().runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> {
                log("&6[DEBUG] Restoring tree at " + location);
                sapling.onGrow(location);
            }, 20L); // Delay to allow chunk loading
        }

        log("&aSaplings successfully loaded into memory: " + saplingMap.size());
    }

    // Save saplings to storage
    public void saveSaplings() {
        Map<GenericLocation, String> serializedSaplings = new HashMap<>();
        saplingMap.forEach((location, sapling) -> {
            if (location != null) {
                String saplingId = sapling.getId().toLowerCase(); // Convert to lowercase
                serializedSaplings.put(new GenericLocation(location), saplingId);
                log("&aStoring sapling ID: " + saplingId + " at " + location);
            }
        });

        SerializableData.Farming.writeToDatabase(serializedSaplings, SAPLING_FILE);
        log("&aSaplings saved successfully. Total saved: " + serializedSaplings.size());
    }

    /*

    ########################################################
    # +--------------------------------------------------+ #
    # |                Sapling Management                | #
    # +--------------------------------------------------+ #
    ########################################################

    */

    // Add a new sapling to the map.
    public void addSapling(Location loc, CustomSapling sapling) {
        saplingMap.put(loc, sapling);
    }

    // Remove a sapling from the map.
    public void removeSapling(Location loc) {
        saplingMap.remove(loc);
    }

    // Get a sapling from the map.
    public CustomSapling getSapling(Location loc) {
        return saplingMap.get(loc);
    }

    // Check if a location has a sapling.
    public boolean hasSapling(Location loc) {
        return saplingMap.containsKey(loc);
    }

    // Check how many saplings exist.
    public int getSaplingCount() {
        return saplingMap.size();
    }

    /*

    #############################################
    # +---------------------------------------+ #
    # |                Logging                | #
    # +---------------------------------------+ #
    #############################################

    */

    // Log messages to the console
    public static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(Component.text("[UnitedItems] ").color(NamedTextColor.BLUE)
                .append(Component.text(msg).color(NamedTextColor.WHITE)));
    }

}
