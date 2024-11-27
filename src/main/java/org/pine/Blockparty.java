package org.pine;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pine.event.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blockparty extends JavaPlugin {

    private static final Logger log = LoggerFactory.getLogger(Blockparty.class);

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getServer().getPluginManager();
        manager.registerEvents(new PlayerEvent(), this);
        log.info("Blockparty plugin loaded");
    }
}