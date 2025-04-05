package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.pine.blockparty.managers.PlatformManager;

import java.util.List;

import static org.pine.blockparty.managers.PlatformManager.Y_LVL;

public class RandomTeleportationSpecialRound implements SpecialRound {

    private static final int TELEPORTATION_INTERVAL_TICKS = 40;
    private static final int INITIAL_DELAY_TICKS = 20;

    @Override
    public Component getMessage() {
        return Component.text("§1§lSomeone opened a portal!");
    }

    @Override
    public void start(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        final int teleportCount = (int) roundDurationInTicks / TELEPORTATION_INTERVAL_TICKS;
        if (teleportCount == 0) {
            createTeleportationTasks(world, playersLeft, plugin, INITIAL_DELAY_TICKS);
        }

        for (int i = 0; i < teleportCount; i++) {
            createTeleportationTasks(world, playersLeft, plugin, INITIAL_DELAY_TICKS + i * TELEPORTATION_INTERVAL_TICKS);
        }
    }

    @Override
    public void stop(World world, List<Player> player, long roundDurationInTicks, Plugin plugin) {
    }

    private void createTeleportationTasks(World world, List<Player> playersLeft, Plugin plugin, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : playersLeft) {
                player.teleport(PlatformManager.randomLocationOnPlatform(Y_LVL + 3, world));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            }
        }, delay);
    }
}
