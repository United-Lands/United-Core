package org.unitedlands.items.tools;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Collections;
import java.util.List;

public abstract class CustomTool {
    // Apply effects to the player
    public void applyEffects(Player player) {
    }

    // Get the list of potion effects applied by this tool
    public List<PotionEffectType> getAppliedEffects() {
        return Collections.emptyList();
    }

    // Handle block break logic for the tool
    public void handleBlockBreak(Player player, BlockBreakEvent event) {
    }

    // Handle interaction logic for the tool
    public void handleInteract(Player player, PlayerInteractEvent event) {
    }

    // Handle damage logic for the tool.
    public void handleEntityDamage(Player player, EntityDamageByEntityEvent event) {
    }
}
