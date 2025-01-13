package org.unitedlands.items.armours;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public abstract class CustomArmour {

    // Apply effects to the player
    public abstract void applyEffects(Player player);

    // Get the list of potion effects applied by this armor
    public abstract List<PotionEffectType> getAppliedEffects();
}