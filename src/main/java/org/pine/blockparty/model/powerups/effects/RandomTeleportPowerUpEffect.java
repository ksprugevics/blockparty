package org.pine.blockparty.model.powerups.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Random;

public class RandomTeleportPowerUpEffect implements PowerUpEffect {

    @Override
    public void apply(Player player) {
        final Random random = new Random();
        player.teleport(new Location(Bukkit.getWorld("world"), random.nextInt(0, 33), 2, random.nextInt(0, 33)));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.chat("Where am I!?");
    }
}
