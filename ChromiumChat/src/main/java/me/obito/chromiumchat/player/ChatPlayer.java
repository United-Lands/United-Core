package me.obito.chromiumchat.player;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.obito.chromiumchat.gradient.Gradient;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ChatPlayer implements Listener {

    private String UUID;


    @EventHandler

    public void onJoin(PlayerJoinEvent e){
        UUID = e.getPlayer().getUniqueId().toString();
        createCustomConfig();
    }





    private void createCustomConfig() {
        File customConfigFile;
        customConfigFile = new File(Bukkit.getPluginManager().getPlugin("ChromiumChat").getDataFolder(), UUID + ".yml");
        FileConfiguration customConfig;
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();

            try{

                customConfigFile.createNewFile();
                customConfig = new YamlConfiguration();
                customConfig.load(customConfigFile);
                customConfig.set("GradientEnabled", true);
                customConfig.set("GradientStart", "#ffffff");
                customConfig.set("GradientEnd", "#000000");
                customConfig.save(customConfigFile);
            } catch (Exception e1){
                System.out.println("NERADI");
                System.out.println("NERADI");
                System.out.println("NERADI");
                System.out.println("NERADI");
                System.out.println("NERADI");
            }
            Bukkit.getPluginManager().getPlugin("ChromiumChat").saveResource(UUID + ".yml", false);
        }
        customConfig = new YamlConfiguration();


        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private Gradient g;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){

        File customConfigFile;
        customConfigFile = new File(Bukkit.getPluginManager().getPlugin("ChromiumChat").getDataFolder(), UUID + ".yml");
        FileConfiguration customConfig;
        customConfig = new YamlConfiguration();
        try{
            customConfig.load(customConfigFile);
        } catch (Exception e2){

        }

        if(customConfig.getBoolean("GradientEnabled")){
            ArrayList<String> colors = new ArrayList<>();
            try {

                colors.add(customConfig.getString("GradientStart"));
                colors.add(customConfig.getString("GradientEnd"));
                Gradient g = new Gradient(colors);
                e.setMessage(g.gradientMessage(e.getMessage(), "", false));

            } catch (Exception e1) {

                System.out.println("Gradient error with player " + e.getPlayer().getName());
                e.setMessage(e.getMessage());

            }


        }



    }


}
