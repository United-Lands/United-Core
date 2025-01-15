package org.unitedlands.items.armours;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public abstract class CustomArmour {

    // Apply effects to the player
    public void applyEffects(Player player) {
    }

    // Get the list of potion effects applied by this armor
    public List<PotionEffectType> getAppliedEffects() {
        return null;
    }
}