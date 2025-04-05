package org.pine.blockparty.model.powerups.effects;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.pine.blockparty.configuration.Configuration;
import org.pine.blockparty.managers.PlatformManager;

public class RandomTeleportPowerUpEffect implements PowerUpEffect {

    @Override
    public void apply(Player player) {
        player.teleport(PlatformManager.randomLocationOnPlatform(PlatformManager.Y_LVL + 2, Bukkit.getWorld(Configuration.WORLD_NAME.getDefaultValue())));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.chat("Where am I!?");
    }
}
