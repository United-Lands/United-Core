package org.unitedlands.items.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.unitedlands.items.crops.CustomCrop;
import org.unitedlands.items.saplings.CustomSapling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataManager {

    private static final String SAPLING_FILE = "sapling.dat";
    private final Map<Location, CustomSapling> saplingMap = new HashMap<>();
    private static final String CROP_FILE = "crops.dat";
    private final Map<Location, CustomCrop> cropMap = new HashMap<>();
    private final Map<Location, Integer> growthStages = new HashMap<>();

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
        HashMap<GenericLocation, String> loadedSaplings = SerializableData.Farming.readFromDatabase("sapling.dat", HashMap.class);
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

            Bukkit.getScheduler().runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnitedItems")), () -> sapling.onGrow(location), 20L); // Delay to allow chunk loading
        }
        log("Saplings successfully loaded into memory: " + saplingMap.size());
    }

    // Save saplings to storage
    public void saveSaplings() {
        Map<GenericLocation, String> serializedSaplings = new HashMap<>();
        saplingMap.forEach((location, sapling) -> {
            if (location != null) {
                String saplingId = sapling.getId().toLowerCase();
                serializedSaplings.put(new GenericLocation(location), saplingId);
            }
        });

        SerializableData.Farming.writeToDatabase(serializedSaplings, SAPLING_FILE);
        log("&aSaplings saved successfully. Total saved: " + serializedSaplings.size());
    }

    @SuppressWarnings("unchecked")
    public void loadCrops(Map<String, CustomCrop> cropSets) {
        Map<GenericLocation, CropData> loadedCrops = SerializableData.Farming.readFromDatabase(CROP_FILE, HashMap.class);
        if (loadedCrops == null || loadedCrops.isEmpty()) {
            log("&aNo cached crops found.");
            return;
        }

        for (Map.Entry<GenericLocation, CropData> entry : loadedCrops.entrySet()) {
            GenericLocation genLoc = entry.getKey();
            CropData cropData = entry.getValue();
            Location location = genLoc.getLocation();
            CustomCrop crop = cropSets.get(cropData.getCropId());
            if (location != null && crop != null) {
                cropMap.put(location, crop);
                growthStages.put(location, cropData.getGrowthStage());
            }
        }
        log("&aCrops successfully loaded into memory: " + cropMap.size());
    }

    public void saveCrops() {
        Map<GenericLocation, CropData> serializedCrops = new HashMap<>();
        cropMap.forEach((loc, crop) -> {
            int stage = growthStages.getOrDefault(loc, 1);
            serializedCrops.put(new GenericLocation(loc), new CropData(crop.getId(), stage));
        });
        SerializableData.Farming.writeToDatabase(serializedCrops, CROP_FILE);
        log("Crops saved successfully. Total saved: " + serializedCrops.size());
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

    #####################################################
    # +-----------------------------------------------+ #
    # |                Crop Management                | #
    # +-----------------------------------------------+ #
    #####################################################

    */

    public void addCrop(Location loc, CustomCrop crop, int stage) {
        cropMap.put(loc, crop);
        growthStages.put(loc, stage);
    }

    public void updateCropStage(Location loc, int stage) {
        growthStages.put(loc, stage);
    }

    public boolean hasCrop(Location loc) {
        return cropMap.containsKey(loc);
    }

    public CustomCrop getCrop(Location loc) {
        return cropMap.get(loc);
    }

    public int getCropStage(Location loc) {
        return growthStages.get(loc);
    }

    public void removeCrop(Location loc) {
        cropMap.remove(loc);
        growthStages.remove(loc);
    }

    // Check how many crops exist.
    public int getCropCount() {
        return cropMap.size();
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
        Bukkit.getConsoleSender().sendMessage(Component.text("[UnitedItems] " + msg).color(NamedTextColor.WHITE));
    }
}
