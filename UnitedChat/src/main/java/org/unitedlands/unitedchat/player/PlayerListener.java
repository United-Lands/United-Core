package org.unitedlands.unitedchat.player;

import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.unitedlands.unitedchat.UnitedChat;
import org.unitedlands.unitedchat.gradient.Gradient;
import org.unitedlands.unitedchat.gradient.GradientPresets;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {


    @EventHandler

    public void onJoin(PlayerJoinEvent e){

        FileConfiguration conf = UnitedChat.getConfigur();

        List<String> motd = UnitedChat.getList("Motd");
        List<String> firstmotd = UnitedChat.getList("FirstJoinMotd");

        Player p = e.getPlayer();

        p.sendMessage((StringUtils.repeat(" \n", 150)));

        if(p.hasPlayedBefore()){
            for(String s : motd){
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', s)));
            }
        } else {
            for(String s : firstmotd){
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', s)));
            }
        }


        try{
            File customConfigFile;
            FileConfiguration customConfig;
            customConfigFile = new File(Bukkit.getPluginManager().getPlugin("UnitedCore").getDataFolder(), "/players/" + e.getPlayer().getUniqueId() + ".yml");
            if (!customConfigFile.exists()) {
                customConfigFile.getParentFile().mkdirs();

                try {

                    customConfigFile.createNewFile();
                    customConfig = new YamlConfiguration();
                    customConfig.load(customConfigFile);
                    customConfig.set("Player Name", e.getPlayer().getName());
                    customConfig.set("GradientEnabled", false);
                    customConfig.set("GradientStart", "#ffffff");
                    customConfig.set("GradientEnd", "#ffffff");
                    customConfig.set("GradientPreset", "none");
                    customConfig.set("PvP", false);
                    customConfig.save(customConfigFile);

                } catch (Exception e1){

                }
            }
        } catch (Exception e1){

        }



    }




    @EventHandler
    public void onChat(AsyncChatHookEvent e){

        File customConfigFile;
        customConfigFile = new File(Bukkit.getPluginManager().getPlugin("UnitedCore").getDataFolder(), "/players/" + e.getPlayer().getUniqueId() + ".yml");
        FileConfiguration customConfig;
        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (Exception e2){

        }

        if (!e.getChannel().getName().equals("general")) {
            return;
        }

        if(customConfig.getBoolean("GradientEnabled") ){

            if(customConfig.getString("GradientPreset").equals("none")){
                ArrayList<String> colors = new ArrayList<>();
                try {

                    colors.add(customConfig.getString("GradientStart"));
                    colors.add(customConfig.getString("GradientEnd"));
                    Gradient g = new Gradient(colors);
                    e.setMessage(g.gradientMessage(e.getMessage(), "", false));

                } catch (Exception e1) {

                    e.setMessage(e.getMessage());

                }
            } else {
                try {

                    Gradient g = GradientPresets.getGradient(customConfig.getString("GradientPreset"));
                    e.setMessage(g.gradientMessage(e.getMessage(), "", false));

                } catch (Exception e1) {
                    
                    e.setMessage(e.getMessage());

                }
            }




        } else {

            String messageDefault = UnitedChat.getConfigur().getString("Default Message Color") + e.getMessage();
            String messageColored = (ChatColor.translateAlternateColorCodes('&', messageDefault));
            String messageFinal = messageColored;
            for(Player p : Bukkit.getOnlinePlayers()){
                if(messageDefault.toLowerCase().contains(p.getName().toLowerCase())){
                    messageFinal = messageColored.replace(p.getName(), (ChatColor.translateAlternateColorCodes('&', UnitedChat.getConfigur()
                            .getString("Player Mention Color") + p.getName() + UnitedChat.getConfigur().getString("Default Message Color"))));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                }
            }
            e.setMessage(messageFinal);
        }



    }


}