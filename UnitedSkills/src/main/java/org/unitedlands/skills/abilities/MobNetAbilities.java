package org.unitedlands.skills.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.unitedlands.skills.UnitedSkills;
import org.unitedlands.skills.Utils;
import org.unitedlands.skills.skill.Skill;
import org.unitedlands.skills.skill.SkillType;

import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import com.destroystokyo.paper.ParticleBuilder;
import com.palmergames.bukkit.towny.TownyAPI;

import de.tr7zw.nbtapi.NBT;

public class MobNetAbilities implements Listener {

    private final UnitedSkills unitedSkills;
    private Player player;

    public MobNetAbilities(UnitedSkills unitedSkills) {
        this.unitedSkills = unitedSkills;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCatchMob(PlayerInteractEntityEvent event) {

        if (event.isCancelled())
            return;

        // PlayerInteractEntityEvent is fired twice by the game, once for HAND and once
        // for OFF_HAND. Stop the OFF_HAND event from being executed.
        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        // Only check for living entitites
        if (!(event.getRightClicked() instanceof LivingEntity))
            return;

        // Don't consider players for catching -_-
        if (event.getRightClicked() instanceof Player)
            return;

        player = event.getPlayer();

        var mob = (LivingEntity) event.getRightClicked();

        var itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null)
            return;

        // Check to see if the item is a custom ItemsAdder item.
        var captureItem = CustomStack.byItemStack(itemInHand);
        if (captureItem == null)
            return;

        // Check for the correct items
        var isHostileMobNet = captureItem
                .matchNamespacedID(CustomStack.getInstance("unitedlands:empty_hostile_mob_net"));
        var isPassiveMobNet = captureItem
                .matchNamespacedID(CustomStack.getInstance("unitedlands:empty_passive_mob_net"));

        if (!isHostileMobNet && !isPassiveMobNet)
            return;

        if (isHostileMobNet && !isHunter()) {
            player.sendMessage(Utils.getMessage("must-be-hunter"));
            return;
        }
        if (isPassiveMobNet && !isFarmer()) {
            player.sendMessage(Utils.getMessage("must-be-farmer"));
            return;
        }

        // Check if player has Towny permissions to catch mobs in the provided location
        var towny = TownyAPI.getInstance();
        if (towny != null) {
            var location = event.getRightClicked().getLocation();

            // Catching in the wilderness is allowed by default, only perform checks when in
            // a town.
            if (!towny.isWilderness(location)) {
                var town = towny.getTown(location);
                var resident = TownyAPI.getInstance().getResident(player);
                if (town != null && resident != null) {
                    // Only check further in non-ruined towns
                    if (!town.isRuined()) {
                        // If player is not in their own town, check the trust list
                        if (!resident.hasTown() || (resident.hasTown() && !resident.getTownOrNull().equals(town))) {
                            var trustList = town.getTrustedResidents();
                            if (!trustList.contains(resident)) {
                                player.sendMessage(Utils.getMessage("no-catch-permission-town"));
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (isHostileMobNet && isHunter()) {
            Skill trafficker = new Skill(player, SkillType.TRAFFICKER);
            if (trafficker.getLevel() == 0) {
                player.sendMessage(Utils.getMessage("must-have-trafficker"));
                return;
            }

            if (!isInEntityList(mob, "trafficker-mobs")) {
                player.sendMessage(Utils.getMessage("entity-not-in-list-trafficker"));
                return;
            } else {
                event.setCancelled(true);
                catchMob("hostile", itemInHand, mob);
            }

        }

        if (isPassiveMobNet && isFarmer()) {
            Skill wrangler = new Skill(player, SkillType.WRANGLER);
            if (wrangler.getLevel() == 0) {
                player.sendMessage(Utils.getMessage("must-have-wrangler"));
                return;
            }

            if (!isInEntityList(mob, "wrangler-mobs")) {
                player.sendMessage(Utils.getMessage("entity-not-in-list-wrangler"));
                return;
            } else {
                event.setCancelled(true);
                catchMob("passive", itemInHand, mob);
            }
        }
    }

    private void catchMob(String type, ItemStack itemInHand, LivingEntity mob) {
        int currentAmount = itemInHand.getAmount();
        if (currentAmount > 1) {
            itemInHand.setAmount(currentAmount - 1);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }

        try {
            var fullMobNet = createFullMobNet(mob, type);
            var overflow = player.getInventory().addItem(fullMobNet);
            if (overflow != null && !overflow.isEmpty())
                for (var item : overflow.values())
                    player.getWorld().dropItem(player.getLocation().add(0,1,0), item);
                
            ParticleBuilder particles = new ParticleBuilder(Particle.VILLAGER_HAPPY);
            particles.count(100).location(mob.getLocation()).offset(1, 1, 1).spawn();
            player.playSound(player.getLocation(), "ui.toast.in", 1.0f, 1.0f);

        } catch (Exception ex) {
            player.sendMessage("<red>Error while trying to create Mob Net: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        mob.remove();
    }

    private ItemStack createFullMobNet(LivingEntity mob, String type) {

        ItemStack fullCaptureItem = CustomStack.getInstance("unitedlands:full_" + type + "_mob_net").getItemStack();
        fullCaptureItem.setAmount(1);

        String customName = NBT.get(mob, nbt -> (String) nbt.getString("CustomName"));
        Integer age = NBT.get(mob, nbt -> (Integer) nbt.getInteger("Age"));
        String variant = NBT.get(mob, nbt -> (String) nbt.getString("variant"));
        Integer variant2 = NBT.get(mob, nbt -> (Integer) nbt.getInteger("Variant"));
        String mtype = NBT.get(mob, nbt -> (String) nbt.getString("Type"));
        Byte collarColor = NBT.get(mob, nbt -> (Byte) nbt.getByte("CollarColor"));
        int[] owner = NBT.get(mob, nbt -> (int[]) nbt.getIntArray("Owner"));
        Byte tame = NBT.get(mob, nbt -> (Byte) nbt.getByte("Tame"));

        NBT.modify(fullCaptureItem, nbt -> {
            nbt.setString("ulc_type", mob.getType().name());
            nbt.setString("ulc_customName", customName);
            nbt.setInteger("ulc_age", age);
            nbt.setString("ulc_variant", variant);
            nbt.setInteger("ulc_variant2", variant2);
            nbt.setString("ulc_mtype", mtype);
            nbt.setByte("ulc_collarColor", collarColor);
            nbt.setIntArray("ulc_owner", owner);
            nbt.setByte("ulc_tame", tame);
            nbt.setLong("ulc_catchtime", System.currentTimeMillis());
        });

        Component mobInfo = Component.text("Contains: ").color(NamedTextColor.BLUE);

        // Set full Mob Net display info
        if (customName != null && customName != "") {
            // Try to extract the custom name from the JSON string
            Pattern namePattern = Pattern.compile("\"extra\":\\[\"(.*?)\"\\]", Pattern.CASE_INSENSITIVE);
            Matcher matcher = namePattern.matcher(customName);
            if (matcher.find()) {
                mobInfo = mobInfo
                        .append(Component.text(matcher.group(1) + " (" + mob.getType().name().toLowerCase() + ")",
                                NamedTextColor.GREEN));
            } else {
                mobInfo = mobInfo.append(Component.text(mob.getType().name().toLowerCase()));
            }
        } else {
            mobInfo = mobInfo.append(Component.text(mob.getType().name().toLowerCase()));
        }

        List<Component> mobInfoList = new ArrayList<>();
        mobInfoList.add(mobInfo);

        fullCaptureItem.lore(mobInfoList);

        return fullCaptureItem;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onReleaseMob(PlayerInteractEvent event) {

        // PlayerInteractEvent is fired twice by the game, once for HAND and once
        // for OFF_HAND. Stop the OFF_HAND event from being executed.
        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            player = event.getPlayer();
            var itemInHand = player.getInventory().getItemInMainHand();

            // Check to see if the item is a custom ItemsAdder item.
            var captureItem = CustomStack.byItemStack(itemInHand);
            if (captureItem == null)
                return;

            // Check for the correct items
            var isHostileMobNet = captureItem
                    .matchNamespacedID(CustomStack.getInstance("unitedlands:full_hostile_mob_net"));
            var isPassiveMobNet = captureItem
                    .matchNamespacedID(CustomStack.getInstance("unitedlands:full_passive_mob_net"));

            if (!isHostileMobNet && !isPassiveMobNet)
                return;

            event.setCancelled(true);

            // Calculate spawn position
            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection();
            Location spawnBlockLocation = player.getTargetBlockExact(6) != null
                    ? player.getTargetBlockExact(10).getLocation().add(0, 1, 0)
                    : eyeLocation.add(direction.multiply(6));

            String capturedMobType = NBT.get(itemInHand, nbt -> (String) nbt.getString("ulc_type"));
            if (capturedMobType == null || capturedMobType == "") {
                player.sendMessage("<red>Error reading type of captured mob!");
                return;
            }

            // If spawn location is valid, spawn mob
            if (spawnBlockLocation != null && spawnBlockLocation.getBlock().getType() == Material.AIR) {

                long catchTime = NBT.get(itemInHand, nbt -> (long) nbt.getLong("ulc_catchtime"));
                if (System.currentTimeMillis() - catchTime <= 500)
                    return;
                
                var spawnLocation = spawnBlockLocation.add(0.5, 0.5, 0.5);
                var newMob = player.getWorld().spawnEntity(spawnLocation, EntityType.valueOf(capturedMobType));

                String customName = NBT.get(itemInHand, nbt -> (String) nbt.getString("ulc_customName"));
                Integer age = NBT.get(itemInHand, nbt -> (Integer) nbt.getInteger("ulc_age"));
                String variant = NBT.get(itemInHand, nbt -> (String) nbt.getString("ulc_variant"));
                Integer variant2 = NBT.get(itemInHand, nbt -> (Integer) nbt.getInteger("ulc_variant2"));
                String mtype = NBT.get(itemInHand, nbt -> (String) nbt.getString("ulc_mtype"));
                Byte collarColor = NBT.get(itemInHand, nbt -> (Byte) nbt.getByte("ulc_collarColor"));
                int[] owner = NBT.get(itemInHand, nbt -> (int[]) nbt.getIntArray("ulc_owner"));
                Byte tame = NBT.get(itemInHand, nbt -> (Byte) nbt.getByte("ulc_tame"));

                NBT.modify(newMob, nbt -> {
                    if (customName != null && customName != "")
                        nbt.setString("CustomName", customName);
                    if (age != null)
                        nbt.setInteger("Age", age);
                    if (variant != null && variant != "")
                        nbt.setString("variant", variant);
                    if (variant2 != null)
                        nbt.setInteger("Variant", variant2);
                    if (mtype != null && mtype != "")
                        nbt.setString("Type", mtype);
                    if (collarColor != null)
                        nbt.setByte("CollarColor", collarColor);
                    if (owner != null)
                        nbt.setIntArray("Owner", owner);
                    if (tame != null)
                        nbt.setByte("Tame", tame);
                });

                int currentAmount = itemInHand.getAmount();
                if (currentAmount > 1) {
                    itemInHand.setAmount(currentAmount - 1);
                } else {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }

                ParticleBuilder particles = new ParticleBuilder(Particle.VILLAGER_HAPPY);
                particles.count(100).location(spawnLocation).offset(1, 1, 1).spawn();
                player.playSound(player.getLocation(), "ui.toast.out", 1.0f, 1.0f);

            } else {
                player.sendMessage("<yellow>This is not a valid spawn location!");
            }

        }
    }

    private boolean isHunter() {
        return Utils.isInJob(player, "Hunter");
    }

    private boolean isFarmer() {
        return Utils.isInJob(player, "Farmer");
    }

    private boolean isInEntityList(Entity entity, String listName) {
        FileConfiguration config = unitedSkills.getConfig();
        List<String> entities = config.getStringList(listName);
        return entities.contains(entity.getType().toString());
    }

}
