package org.unitedlands.items.tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class BarkbinderAxe extends CustomTool {

    public BarkbinderAxe(Plugin plugin) {
    }

    private static final Map<Material, Material> STRIPPED_TO_UNSTRIPPED = new HashMap<>();

    static {
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_OAK_LOG, Material.OAK_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_SPRUCE_LOG, Material.SPRUCE_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_BIRCH_LOG, Material.BIRCH_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_JUNGLE_LOG, Material.JUNGLE_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_ACACIA_LOG, Material.ACACIA_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_DARK_OAK_LOG, Material.DARK_OAK_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_MANGROVE_LOG, Material.MANGROVE_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_CHERRY_LOG, Material.CHERRY_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_PALE_OAK_LOG, Material.PALE_OAK_LOG);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_CRIMSON_STEM, Material.CRIMSON_STEM);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_WARPED_STEM, Material.WARPED_STEM);

        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_OAK_WOOD, Material.OAK_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_SPRUCE_WOOD, Material.SPRUCE_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_BIRCH_WOOD, Material.BIRCH_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_JUNGLE_WOOD, Material.JUNGLE_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_ACACIA_WOOD, Material.ACACIA_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_DARK_OAK_WOOD, Material.DARK_OAK_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_MANGROVE_WOOD, Material.MANGROVE_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_CHERRY_WOOD, Material.CHERRY_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_PALE_OAK_WOOD, Material.PALE_OAK_WOOD);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_CRIMSON_HYPHAE, Material.CRIMSON_HYPHAE);
        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_WARPED_HYPHAE, Material.WARPED_HYPHAE);

        STRIPPED_TO_UNSTRIPPED.put(Material.STRIPPED_BAMBOO_BLOCK, Material.BAMBOO_BLOCK);
    }

    @Override
    public void handleInteract(Player player, PlayerInteractEvent event) {

        // Only continue if a block was right-clicked.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        Block block = event.getClickedBlock();

        ItemStack item = player.getInventory().getItemInMainHand();

        Material strippedMaterial = block.getType();
        if (!STRIPPED_TO_UNSTRIPPED.containsKey(strippedMaterial)) {
            return;
        }

        // Check if the block is a stripped wood block.
        Material unstrippedMaterial = STRIPPED_TO_UNSTRIPPED.get(strippedMaterial);
        block.setType(unstrippedMaterial);

        damageItem(item);
    }

    private void damageItem(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damageable.getDamage() + 1);
                item.setItemMeta(meta);
            }
        }

    }
}
