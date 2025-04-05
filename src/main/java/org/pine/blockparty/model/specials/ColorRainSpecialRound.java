package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.pine.blockparty.model.XBlock;

import java.util.List;

import static org.pine.blockparty.managers.PlatformManager.Y_LVL;
import static org.pine.blockparty.managers.PlatformManager.randomLocationOnPlatform;

public class ColorRainSpecialRound implements SpecialRound {

    private static final long BLOCK_CHANGE_INTERVAL_TICKS = 1;

    @Override
    public Component getMessage() {
        return Component.text("§cI§6t§e'§a §br§3a§9i§dn§ci§6n§5g §7c§8o§0l§fo§4r§c!");
    }

    @Override
    public void start(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        playSound(playersLeft);
        world.setStorm(true);
        createBlockChangeTasks(world, roundDurationInTicks, plugin);
    }

    @Override
    public void stop(World world, List<Player> player, long roundDurationInTicks, Plugin plugin) {
        world.setStorm(false);
    }

    private void playSound(List<Player> players) {
        players.stream().forEach(pl -> pl.playSound(pl.getLocation(), Sound.WEATHER_RAIN, 0.7f, 1.0f));
    }

    private static void createBlockChangeTasks(World world, long roundDurationInTicks, Plugin plugin) {
        final long blockCountToChange = roundDurationInTicks / BLOCK_CHANGE_INTERVAL_TICKS;

        for (long i = 0; i < blockCountToChange; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                final Material randomColor = XBlock.randomBlock().getMaterial();
                world.getBlockAt(randomLocationOnPlatform(Y_LVL, world)).setType(randomColor);
            }, i * BLOCK_CHANGE_INTERVAL_TICKS);
        }
    }
}
