package org.unitedlands.items.armours;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class NutcrackerArmour extends CustomArmour {

    @Override
    public void applyEffects(Player player) {
        // Apply effects when wearing the Nutcracker set
        PotionEffect[] effects = {
                new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1, true, false),
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false),
                new PotionEffect(PotionEffectType.HASTE, Integer.MAX_VALUE, 0, true, false),
                new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, true, false),
        };

        for (PotionEffect effect : effects) {
            if (!player.hasPotionEffect(effect.getType())) {
                player.addPotionEffect(effect);
            }
        }
    }

    @Override
    public List<PotionEffectType> getAppliedEffects() {
        return Arrays.asList(
                PotionEffectType.STRENGTH,
                PotionEffectType.SPEED,
                PotionEffectType.HASTE,
                PotionEffectType.REGENERATION
        );
    }
}
