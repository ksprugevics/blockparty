package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Random;

import static org.pine.blockparty.managers.PlatformManager.X_MAX;
import static org.pine.blockparty.managers.PlatformManager.Y_LVL;
import static org.pine.blockparty.managers.PlatformManager.Z_MAX;

public class RandomTeleportationSpecialRound implements SpecialRound {

    private static final int TELEPORTATION_INTERVAL_TICKS = 40;
    private static final int INITIAL_DELAY_TICKS = 20;
    private static final Random random = new Random();

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
                player.teleport(new Location(world, random.nextInt(X_MAX + 1), Y_LVL + 3, random.nextInt(Z_MAX + 1)));
            }
        }, delay);
    }
}
