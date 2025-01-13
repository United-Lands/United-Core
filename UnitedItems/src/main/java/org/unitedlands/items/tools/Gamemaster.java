package org.unitedlands.items.tools;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class Gamemaster extends CustomTool {
    @Override
    public void applyEffects(Player player) {
        // Apply tool-specific effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 0, true, false)); // Luck
    }
    @Override
    public List<PotionEffectType> getAppliedEffects() {
        // Return the effects applied by this tool
        return Collections.singletonList(PotionEffectType.LUCK);
    }
}
