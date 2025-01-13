package org.unitedlands.items.tools;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public abstract class CustomTool {
    // Apply effects to the player
    public abstract void applyEffects(Player player);

    // Get the list of potion effects applied by this tool
    public abstract List<PotionEffectType> getAppliedEffects();
}
