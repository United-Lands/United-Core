package org.unitedlands.unitedchat;

import org.unitedlands.unitedchat.commands.ClearChatCmd;
import org.unitedlands.unitedchat.commands.GradientCmd;
import org.unitedlands.unitedchat.gradient.GradientPresets;
import org.unitedlands.unitedchat.player.PlayerListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class UnitedChat extends JavaPlugin {

    private Logger log;
    File customConfigFile;
    FileConfiguration customConfig;
    public static FileConfiguration Config1;
    int i = 1;

    @Override
    public void onEnable(){

        log = getLogger();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().getPlugin("UnitedChat").saveDefaultConfig();
            Config1 = getConfig();
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
            this.getCommand("gradient").setExecutor(new GradientCmd());
            this.getCommand("cc").setExecutor(new ClearChatCmd());
            GradientPresets.loadPredefinedGradients(Config1);


            customConfigFile = new File(Bukkit.getPluginManager().getPlugin("UnitedCore").getDataFolder(), "messages.yml");
            if (!customConfigFile.exists()) {
                customConfigFile.getParentFile().mkdirs();

                try {

                    customConfigFile.createNewFile();
                    customConfig = new YamlConfiguration();
                    customConfig.load(customConfigFile);

                    List<String> motd = new ArrayList<>();

                    motd.add("&aWelcome");
                    motd.add("&bto");
                    motd.add("&cUnitedLands");

                    List<String> defaultMotd = new ArrayList<>();

                    // default motd

                    defaultMotd.add("&aWelcome");
                    defaultMotd.add("&eto");
                    defaultMotd.add("&3UnitedLands");

                    // default messages

                    customConfig.createSection("Global");
                    customConfig.getConfigurationSection("Global").set("Motd", motd);
                    customConfig.getConfigurationSection("Global").set("FirstJoinMotd", defaultMotd);
                    customConfig.getConfigurationSection("Global").set("NoPerm", "&cYou don't have permission.");
                    customConfig.getConfigurationSection("Global").set("PlayerNotRecognized", "&cCan't recognize player.");
                    customConfig.getConfigurationSection("Global").set("ConfError", "&cError with configuration.");
                    customConfig.createSection("Chat");
                    customConfig.getConfigurationSection("Chat").set("ChatCleared", "&bGlobal Chat Cleared.");
                    customConfig.getConfigurationSection("Chat").set("GradientChanged", "&eGradient changed.");
                    customConfig.getConfigurationSection("Chat").set("GradientOn", "&eGradient enabled.");
                    customConfig.getConfigurationSection("Chat").set("GradCommand", "&eUse /gradient <toggle> | <preset> | <hexcolor1> <hexcolor2> %player_name%");
                    customConfig.getConfigurationSection("Chat").set("GradAdminCommand", "&eUse /gradient <toggle> | <preset> | <hexcolor1> <hexcolor2> | <player>");
                    customConfig.getConfigurationSection("Chat").set("GradientOff", "&eGradient disabled.");
                    customConfig.getConfigurationSection("Chat").set("GradientOff", "&cYour gradient is disabled.");
                    customConfig.getConfigurationSection("Chat").set("GradientUnknownPreset", "&eGradient preset not recognized.");
                    customConfig.createSection("PvP");
                    customConfig.getConfigurationSection("PvP").set("InCombat", "&cYou can't use that command while in combat.");
                    customConfig.getConfigurationSection("PvP").set("PvPDisabled", "&ePvP disabled.");
                    customConfig.getConfigurationSection("PvP").set("PvPEnabled", "&ePvP enabled.");
                    customConfig.getConfigurationSection("PvP").set("PvPCommand", "&eUse /pvp <on/off> | <status>.");
                    customConfig.getConfigurationSection("PvP").set("PvPAdminCommand", "&eUse /pvp <on/off> | <player> | <status>.");
                    customConfig.getConfigurationSection("PvP").set("PvPEnabledOp", "&ePvP enabled for player.");
                    customConfig.getConfigurationSection("PvP").set("PvPDisabledOp", "&ePvP disabled for player.");
                    customConfig.getConfigurationSection("PvP").set("PvPEnabledByAdmin", "&ePvP enabled by admin.");
                    customConfig.getConfigurationSection("PvP").set("PvPDisabledByAdmin", "&ePvP disabled by admin.");
                    customConfig.getConfigurationSection("PvP").set("PvPAlreadyOn", "&cPvP already enabled.");
                    customConfig.getConfigurationSection("PvP").set("PvPAlreadyOff", "&cPvP already disabled.");
                    customConfig.getConfigurationSection("PvP").set("PvPStatusOn", "&ePvP status: ON");
                    customConfig.getConfigurationSection("PvP").set("PvPStatusOff", "&cPvP status: OFF");
                    customConfig.getConfigurationSection("PvP").set("FFTownDisabled", "&6Friendly Fire in town disabled.");
                    customConfig.getConfigurationSection("PvP").set("FFTownEnabled", "&6Friendly Fire in town enabled.");
                    customConfig.getConfigurationSection("PvP").set("FFNationDisabled", "&6Friendly Fire in nation disabled.");
                    customConfig.getConfigurationSection("PvP").set("NoNation", "&cYou don't belong to any nation.");
                    customConfig.getConfigurationSection("PvP").set("NoTown", "&cYou don't belong to any town.");
                    customConfig.getConfigurationSection("PvP").set("FFNationEnabled", "&6Friendly Fire in nation enabled.");
                    customConfig.getConfigurationSection("PvP").set("GlobalFFEnabled", "&6Friendly Fire globally enabled!");
                    customConfig.getConfigurationSection("PvP").set("GlobalFFDisabled", "&6Friendly Fire globally disabled!");
                    customConfig.createSection("Items");
                    customConfig.getConfigurationSection("Items").set("ReceivedSapling", "&eYou have received a sapling.");
                    customConfig.getConfigurationSection("Items").set("InvalidTree", "&cInvalid tree.");
                    customConfig.save(customConfigFile);

                } catch (Exception e1){
                    log.warning("[Exception] Can't create new file or load it");
                }


            }
            int seconds = Config1.getInt("TimeInSeconds");
            BukkitScheduler scheduler = getServer().getScheduler();
            scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    if(Config1.getBoolean("BroadcastEnabled") == true) {
                        try {

                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Config1.getString("Message " + i)));
                            i++;


                        } catch (Exception e1){
                            i = 2;
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Config1.getString("Message 1")));
                        }
                    }


                }
            }, 0L, 20L*seconds);
        } else {
            getLogger().warning("[Exception] PlaceholderAPI is required!");
            Bukkit.getPluginManager().disablePlugin(this);
        }



    }
    public static Boolean getConfigurBool(String s){
        return Config1.getBoolean(s);
    }

    public static void setConfigBool(String s, Boolean b){
        Config1.set(s, b);
    }

    public static FileConfiguration getConfigur(){
        return Config1;
    }

    public static String getMsg(String s){
        File customConfigFile;
        customConfigFile = new File(Bukkit.getPluginManager().getPlugin("UnitedCore").getDataFolder(),
                "messages.yml");
        FileConfiguration customConfig;
        customConfig = new YamlConfiguration();
        try{
            customConfig.load(customConfigFile);
        } catch (Exception e2){

        }

        return customConfig.getConfigurationSection("Chat").getString(s);

    }

    public static String getGlobalMsg(String s){
        File customConfigFile;
        customConfigFile = new File(Bukkit.getPluginManager().getPlugin("UnitedCore").getDataFolder(),
                "messages.yml");
        FileConfiguration customConfig;
        customConfig = new YamlConfiguration();
        try{
            customConfig.load(customConfigFile);
        } catch (Exception e2){

        }

        return customConfig.getConfigurationSection("Global").getString(s);

    }

    public static List<String> getList(String s){
        File customConfigFile;
        customConfigFile = new File(Bukkit.getPluginManager().getPlugin("UnitedCore").getDataFolder(),
                "messages.yml");
        FileConfiguration customConfig;
        customConfig = new YamlConfiguration();
        try{
            customConfig.load(customConfigFile);
        } catch (Exception e2){

        }

        return customConfig.getConfigurationSection("Global").getStringList(s);

    }



    @Override
    public void onDisable(){

    }
}