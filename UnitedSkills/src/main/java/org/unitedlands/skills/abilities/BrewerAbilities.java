package org.unitedlands.skills.abilities;

import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.skills.UnitedSkills;
import org.unitedlands.skills.Utils;
import org.unitedlands.skills.skill.Skill;
import org.unitedlands.skills.skill.SkillType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.unitedlands.skills.Utils.*;

public class BrewerAbilities implements Listener {
    private final UnitedSkills unitedSkills;
    private Player player;

    public BrewerAbilities(UnitedSkills unitedSkills) {
        this.unitedSkills = unitedSkills;
    }

    @EventHandler
    public void onHarmfulPotion(EntityPotionEffectEvent event) {
        PotionEffect effect = event.getNewEffect();
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        player = (Player) entity;
        if (isBrewer()) {
            return;
        }
        if (effect == null) {
            return;
        }
        if (!isHarmfulEffect(effect)) {
            return;
        }
        Skill exposureTherapy = new Skill(player, SkillType.EXPOSURE_THERAPY);
        if (exposureTherapy.isSuccessful()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 1f);
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        player = event.getPlayer();
        if (isBrewer()) {
            return;
        }

        ItemStack item = event.getItem();
        if (!item.getType().equals(Material.POTION)) {
            return;
        }
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        PotionData potionData = potionMeta.getBasePotionData();
        final PotionType potionType = potionData.getType();
        Skill assistedHealing = new Skill(player, SkillType.ASSISTED_HEALING);
        if (potionType.equals(PotionType.INSTANT_HEAL) && assistedHealing.getLevel() > 0) {
            increaseHealingEffect(potionMeta);
        }

        Skill exposureTherapy = new Skill(player, SkillType.EXPOSURE_THERAPY);
        if (potionType.equals(PotionType.INSTANT_DAMAGE)) {
            if (exposureTherapy.isSuccessful()) {
                player.getInventory().remove(item);
                player.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 1f);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotionBrew(BrewEvent event) {
        Block block = event.getBlock();
        BrewingStand brewingStand = (BrewingStand) block.getState();
        player = getBrewingStandOwner(block);
        if (player == null) {
            return;
        }
        Skill qualityIngredients = new Skill(player, SkillType.QUALITY_INGREDIENTS);
        if (qualityIngredients.getLevel() > 0) {
            return;
        }
        for (ItemStack item : event.getContents()) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            PotionData basePotionData = potionMeta.getBasePotionData();

            if (basePotionData.isUpgraded()) {
                return;
            }
            PotionType potionType = basePotionData.getType();
            PotionData potionData = new PotionData(potionType, basePotionData.isExtended(), true);
            potionMeta.setBasePotionData(potionData);
            item.setItemMeta(potionMeta);
            brewingStand.update();
        }
    }

    @EventHandler
    public void onBrewingStart(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!inventory.getType().equals(InventoryType.BREWING)) {
            return;
        }
        player = (Player) event.getWhoClicked();
        if (isBrewer()) {
            return;
        }
        Skill modifiedHardware = new Skill(player, SkillType.MODIFIED_HARDWARE);
        if (modifiedHardware.getLevel() == 0) {
            return;
        }
        Block brewingStandBlock = Objects.requireNonNull(inventory.getLocation()).getBlock();
        BrewingStand brewingStand = (BrewingStand) brewingStandBlock.getState(false);
        if (!ownsBrewingStand(brewingStandBlock)) {
            return;
        }
        if (canStartBrewing(brewingStand)) {
            return;
        }

        unitedSkills.getServer().getScheduler().runTaskLater(unitedSkills, task -> {
            if (canStartBrewing(brewingStand)) {
                return;
            }
            if (brewingStand.getBrewingTime() == 0) {
                return;
            }
            int defaultTime = 20;
            int brewingTime = (int) (defaultTime * (1 - (modifiedHardware.getLevel() * 0.10)));
            brewingStand.setBrewingTime(brewingTime * 20);
            brewingStand.update();
        }, 2);
    }

    private boolean canStartBrewing(BrewingStand brewingStand) {
        if (hasBottleOrPotion(brewingStand.getInventory())) return false;
        if (hasBrewingItem(brewingStand.getInventory())) return false;
        return !hasBlazePowder(brewingStand);
    }

    private boolean hasBottleOrPotion(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item == null) {
                continue;
            }
            if (item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasBrewingItem(@NotNull Inventory inventory) {
        FileConfiguration config = unitedSkills.getConfig();
        @NotNull List<String> brewingItems = config.getStringList("brewing-items");
        ItemStack item = inventory.getItem(3);
        if (item == null) {
            return false;
        }
        return brewingItems.contains(item.getType().toString());
    }

    private boolean hasBlazePowder(BrewingStand brewingStand) {
        ItemStack blazePowderSlot = brewingStand.getInventory().getItem(3);
        if (brewingStand.getFuelLevel() > 0) {
            return true;
        }
        if (blazePowderSlot == null) {
            return false;
        }
        return blazePowderSlot.getType().equals(Material.BLAZE_POWDER);
    }

    @SuppressWarnings("SameReturnValue")
    private Player getBrewingStandOwner(Block block) {
        if (block.hasMetadata("jobsBrewingOwner")) {
            BlockOwnerShip blockOwner = getJobs().getBlockOwnerShip(BlockTypes.BREWING_STAND).orElse(null);
            assert blockOwner != null;
            List<MetadataValue> data = blockOwner.getBlockMetadatas(block);
            if (data.isEmpty()) {
                return null;
            }
            MetadataValue value = data.getFirst();
            String uuid = value.asString();
            Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        }
        return null;
    }

    private boolean ownsBrewingStand(Block block) {
        if (block.hasMetadata("jobsBrewingOwner")) {
            BlockOwnerShip blockOwner = getJobs().getBlockOwnerShip(BlockTypes.BREWING_STAND).orElse(null);
            assert blockOwner != null;
            List<MetadataValue> data = blockOwner.getBlockMetadatas(block);
            if (data.isEmpty()) {
                return false;
            }
            MetadataValue value = data.getFirst();
            String uuid = value.asString();
            return uuid.equals(player.getUniqueId().toString());
        }
        return false;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        PotionMeta potionMeta = event.getPotion().getPotionMeta();
        PotionEffectType potionType = potionMeta.getBasePotionData().getType().getEffectType();
        for (Entity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) {
                return;
            }
            player = (Player) entity;
            if (isBrewer()) {
                return;
            }

            Skill exposureTherapy = new Skill(player, SkillType.EXPOSURE_THERAPY);
            Skill assistedHealing = new Skill(player, SkillType.ASSISTED_HEALING);

            if (potionType == null) {
                if (potionMeta.hasCustomEffects()) {
                    potionMeta.getCustomEffects().forEach(effect -> {
                        if (effect.getType().equals(PotionEffectType.HARM)) {
                            if (exposureTherapy.isSuccessful()) {
                                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 1f);
                                event.setIntensity(player, 0);
                            }
                            return;
                        }
                        if (effect.getType().equals(PotionEffectType.HEAL)) {
                            if (assistedHealing.getLevel() > 0) {
                                event.setIntensity(player, 0);
                                increaseHealingEffect(potionMeta);
                            }
                        }
                    });
                }
                return;
            }
            if (potionType.equals(PotionEffectType.HARM)) {
                if (exposureTherapy.isSuccessful()) {
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 1f);
                    event.setIntensity(player, 0);
                }
            }

            if (assistedHealing.getLevel() > 0) {
                if (potionType.equals(PotionEffectType.HEAL)) {
                    event.setIntensity(player, 0);
                    increaseHealingEffect(potionMeta);
                    return;
                }
            }
        }
    }

    private void increaseHealingEffect(PotionMeta potionMeta) {
        Skill skill = new Skill(player, SkillType.ASSISTED_HEALING);
        int skillLevel = skill.getLevel();
        double health = player.getHealth();
        int healthModifier = skillLevel + 1;
        if (potionMeta.getBasePotionData().isUpgraded()) {
            player.setHealth(Math.min(20, health + ((healthModifier + 2) * skillLevel * 2)));
            return;
        }
        player.setHealth(Math.min(20, health + healthModifier * skillLevel * 2));
    }

    private boolean isHarmfulEffect(PotionEffect effect) {
        @NotNull List<String> harmfulPotions = unitedSkills.getConfig().getStringList("harmful-potions");
        return harmfulPotions.contains(effect.getType().getName());
    }

    private boolean isBrewer() {
        return !Utils.isInJob(player, "Brewer");
    }
}
