package org.unitedlands.items.saplings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.unitedlands.skills.skill.Skill;
import org.unitedlands.skills.skill.SkillType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class AncientOak extends CustomSapling {

    public AncientOak() {
        super("ancient_oak",
                Material.OAK_SAPLING,
                Material.OAK_LOG, "trees:ancient_oak_log", false,
                Material.PAPER, "trees:ancient_oak_leaves", false,
                0.00);
    }

    @Override
    public void onPlant(Player player, Location location) {
        Skill ancientOak = new Skill(player, SkillType.ANCIENT_OAK);
        if (ancientOak.getLevel() == 0) {
            player.sendActionBar(Component.text("You must unlock the Ancient Oak Planting skill!", NamedTextColor.RED));
        }
    }

    @Override
    public void onGrow(Location location) {
    }

    @Override
    public void onDecay(Location location) {
    }

    @Override
    public void onBreak(Location location, Player player) {
    }
}
