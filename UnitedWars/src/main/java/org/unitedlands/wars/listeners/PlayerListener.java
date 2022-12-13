package org.unitedlands.wars.listeners;

import com.palmergames.bukkit.towny.event.player.PlayerKilledPlayerEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import de.jeff_media.angelchest.AngelChest;
import de.jeff_media.angelchest.AngelChestPlugin;
import de.jeff_media.angelchest.events.AngelChestSpawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.wars.UnitedWars;
import org.unitedlands.wars.Utils;
import org.unitedlands.wars.war.War;
import org.unitedlands.wars.war.WarDataController;
import org.unitedlands.wars.war.WarDatabase;
import org.unitedlands.wars.war.entities.WarringEntity;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static org.unitedlands.wars.Utils.*;
import static org.unitedlands.wars.war.WarUtil.getOpposingEntity;
import static org.unitedlands.wars.war.WarUtil.hasSameWar;

public class PlayerListener implements Listener {
    private final FileConfiguration config;

    public PlayerListener(UnitedWars unitedWars) {
        config = unitedWars.getConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Town town = getPlayerTown(player);
        if (town == null) return;
        if (!town.hasActiveWar()) return;

        if (isBannedWorld(player.getWorld().getName()))
            teleportPlayerToSpawn(player);

        for (String command : config.getStringList("commands-on-login"))
            player.performCommand(command);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Town town = getPlayerTown(player);
        if (town == null)
            return;
        if (!town.hasActiveWar())
            return;
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (!event.hasChangedBlock())
            return;
        if (cause == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT || cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
            return;
        // they can bypass.
        if (player.hasPermission("united.wars.bypass.tp"))
            return;

        War war = WarDatabase.getWar(town);
        if (war == null)
            return;
        // Allow teleportation during war prep time.
        if (war.hasActiveTimer())
            return;

        double distance = event.getFrom().distance(event.getTo());
        // Too small, don't bother.
        if (distance <= 50)
            return;

        event.setCancelled(true);
        player.sendMessage(Utils.getMessage("teleport-cancelled"));
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6F);

    }

    @EventHandler
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
        if (!config.getStringList("banned-commands").contains(event.getMessage())) return;
        Player player = event.getPlayer();
        Town town = getPlayerTown(player);
        if (town == null) return;
        if (town.hasActiveWar()) {
            player.sendMessage(Utils.getMessage("banned-command"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGraveCreation(AngelChestSpawnEvent event) {
        Resident resident = getTownyResident(event.getAngelChest().getPlayer().getUniqueId());
        if (resident == null) return;
        if (resident.hasTown()) {
            if (resident.getTownOrNull().hasActiveWar()) {
                event.getAngelChest().setProtected(false);
            }
        }
    }

    @EventHandler
    public void onPlayerKillPlayer(PlayerKilledPlayerEvent event) {
        Resident killer = getTownyResident(event.getKiller());
        Resident victim = getTownyResident(event.getVictim());

        if (!hasSameWar(killer, victim))
            return;

        // Killer doesn't have lives, return
        if (!WarDataController.hasResidentLives(killer))
            return;

        if (!WarDataController.hasResidentLives(victim))
            return;

        WarringEntity warringEntity = WarDatabase.getWarringEntity(victim.getPlayer());
        if (warringEntity.getWar().hasActiveTimer())
            return;

        decreaseHealth(victim, warringEntity);

        Component message = getPlayerDeathMessage(warringEntity, killer, victim);
        playSounds(warringEntity);
        if (WarDataController.getResidentLives(victim) == 0) {
            notifyWarKick(victim.getPlayer(), warringEntity);
            return;
        }
        warringEntity.getWar().broadcast(message);

    }

    private void decreaseHealth(Resident victim, WarringEntity warringEntity) {
        warringEntity.getWarHealth().decreaseHealth(5);
        warringEntity.getWarHealth().decreaseMaxHealth(5);
        warringEntity.getWarHealth().flash();
        WarDataController.decrementResidentLives(victim);
    }

    private void playSounds(WarringEntity warringEntity) {
        warringEntity.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1));
        WarringEntity enemy = getOpposingEntity(warringEntity);
        enemy.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f));
    }

    private void notifyWarKick(Player player, WarringEntity warringEntity) {
        Title title = getTitle("<dark_red><bold>OUT OF LIVES!", "<red>You've lost all your lives!");
        player.showTitle(title);
        // player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_7, 1f, 1f);

        Component message = getMessage("removed-from-war",
                component("victim", text(player.getName())));

        warringEntity.getWar().broadcast(message);
    }

    @NotNull
    private Component getPlayerDeathMessage(WarringEntity entity, Resident killer, Resident victim) {
        return Utils.getMessage("player-killed",
                component("victim",
                        text(victim.getName())),
                component("killer",
                        text(killer.getName())),
                component("victim-warrer",
                        text(entity.name())));
    }

    @EventHandler
    public void onTotemPop(EntityResurrectEvent event) {
        if (event.isCancelled())
            return;

        LivingEntity entity = event.getEntity();
        if (entity instanceof Player victim) {
            WarringEntity warringEntity = WarDatabase.getWarringEntity(victim);
            if (warringEntity == null)
                return;
            warringEntity.getWarHealth().decreaseHealth(1);

            Component message = Utils.getMessage("totem-pop",
                    component("victim", text(victim.getName())),
                    component("victim-warrer", text(warringEntity.name())));

            warringEntity.getWar().broadcast(message);
        }
    }

    @EventHandler
    public void onGraveInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.SOUL_CAMPFIRE) return;

        AngelChestPlugin plugin = (AngelChestPlugin) Bukkit.getPluginManager().getPlugin("AngelChest");
        AngelChest chest = plugin.getAngelChestAtBlock(event.getClickedBlock());
        if (chest == null) return;

        Resident openingResident = getTownyResident(event.getPlayer());
        Resident graveResident = getTownyResident(chest.getPlayer().getUniqueId());
        if (hasSameWar(openingResident, graveResident)) {
            chest.setProtected(false);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!WarDatabase.hasWar(player))
            return;
        if (isBannedWorld(event.getRespawnLocation().getWorld().getName()))
            Utils.teleportPlayerToSpawn(player);
    }
}
