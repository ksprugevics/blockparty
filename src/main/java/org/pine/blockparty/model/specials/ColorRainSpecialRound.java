package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.pine.blockparty.model.XBlock;

import java.util.List;
import java.util.Random;

import static org.pine.blockparty.managers.PlatformManager.X_MAX;
import static org.pine.blockparty.managers.PlatformManager.Y_LVL;
import static org.pine.blockparty.managers.PlatformManager.Z_MAX;

public class ColorRainSpecialRound implements SpecialRound {

    private static final int BLOCK_CHANGE_INTERVAL_TICKS = 2;
    private static final  Random random = new Random();

    @Override
    public Component getMessage() {
        return Component.text("§cI§6t§e'§a §br§3a§9i§dn§5g §7c§8o§0l§fo§4r§c!");
    }

    @Override
    public void start(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        world.setThundering(true);
        world.setStorm(true); // todo start actual rain - this doesnt work
        final int blockCountToChange = (int) roundDurationInTicks / BLOCK_CHANGE_INTERVAL_TICKS;

        for (int i = 0; i < blockCountToChange; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                final XBlock blockToChange = XBlock.values()[random.nextInt(XBlock.values().length)];
                world.getBlockAt(random.nextInt(X_MAX + 1), Y_LVL, random.nextInt(Z_MAX + 1)).setType(blockToChange.getMaterial());
            }, i * BLOCK_CHANGE_INTERVAL_TICKS);
        }
    }

    @Override
    public void stop(World world, List<Player> player, long roundDurationInTicks, Plugin plugin) {
        world.setStorm(false);
        world.setThundering(false);
    }
}
