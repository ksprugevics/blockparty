package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Random;

import static org.pine.blockparty.managers.PlatformManager.X_MAX;
import static org.pine.blockparty.managers.PlatformManager.Y_LVL;
import static org.pine.blockparty.managers.PlatformManager.Z_MAX;

public class AcidRainSpecialRound implements SpecialRound {

    private static final int BLOCK_CHANGE_INTERVAL_TICKS = 4;
    private static final  Random random = new Random();

    @Override
    public Component getMessage() {
        return Component.text("§a§lAcid rain! Don't stick your tongue out!");
    }

    @Override
    public void start(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        world.setStorm(true);
        world.setThundering(true);
        final int blockCountToChange = (int) roundDurationInTicks / BLOCK_CHANGE_INTERVAL_TICKS;

        for (int i = 0; i < blockCountToChange; i++) {
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> world.getBlockAt(random.nextInt(X_MAX + 1), Y_LVL, random.nextInt(Z_MAX + 1)).setType(Material.AIR), i * BLOCK_CHANGE_INTERVAL_TICKS
            );
        }
    }

    @Override
    public void stop(World world, List<Player> player, long roundDurationInTicks, Plugin plugin) {
        world.setStorm(false);
        world.setThundering(false);
    }
}
