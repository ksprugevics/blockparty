package org.pine;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pine.event.PlayerEvent;
import org.pine.platform.Platform;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && sender.isOp()) {   // todo better OP handling
            switch (command.getName()) {
                case "bpf" -> Platform.platformToBlock(Material.valueOf(args[0]));
                case "bpx" -> Platform.platformXBlock(Material.valueOf(args[0]));
                case "bpr" -> Platform.resetPlatform();
            }

            return true;
        }

        return false;
    }
}