package org.pine.blockparty.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.pine.blockparty.model.TeleportLocation;

import java.util.List;
import java.util.Random;

public class PlatformManager {

    private static final short RGB_MAX = 256;

    private static final short FIREWORK_COUNT = 15;
    private static final long FIREWORK_DELAY = 25L;
    private static final short FIREWORK_POWER_MAX = 2;
    private static final short FIREWORK_POWER_OFFSET = 1;

    private static final short X_MIN = 0;
    private static final short X_MAX = 31;
    private static final short Z_MIN = 0;
    private static final short Z_MAX = 31;
    private static final short Y_LVL = 0;

    private static final Random random = new Random();

    private final World gameWorld;
    private final List<Location> possibleFireworkLaunchLocations;

    public PlatformManager(World gameWorld) {
        this.gameWorld = gameWorld;
        this.possibleFireworkLaunchLocations = TeleportLocation.getStartingLocations().stream().map(TeleportLocation::getLocation).toList();
    }

    public void platformToPattern(Material[][] pattern) {
        for (int i = X_MIN; i <= X_MAX; i++) {
            for (int j = Z_MIN; j <= Z_MAX; j++) {
                gameWorld.getBlockAt(i, Y_LVL, j).setType(pattern[Z_MAX - i][j]);
            }
        }
    }

    public void platformRemoveXBlock(Material xBlock) {
        for (int i = X_MIN; i <= X_MAX; i++) {
            for (int j = Z_MIN; j <= Z_MAX; j++) {
                final Block block = gameWorld.getBlockAt(i, Y_LVL, j);
                if (block.getType() != xBlock) {
                    block.setType(Material.AIR);
                }
            }
        }
    }

    public void startFireworkShow(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            private int count = 0;

            @Override
            public void run() {
                if (count++ >= FIREWORK_COUNT) {
                    return;
                }
                launchRandomFirework();
            }
        }, 0L, FIREWORK_DELAY);
    }

    private void launchRandomFirework() {
        final Location location =  possibleFireworkLaunchLocations.get(random.nextInt(possibleFireworkLaunchLocations.size()));
        final Firework firework = gameWorld.spawn(location, Firework.class);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        final FireworkEffect effect = FireworkEffect.builder()
                .flicker(random.nextBoolean())
                .withColor(getRandomColor(), getRandomColor())
                .withFade(getRandomColor())
                .with(getRandomType())
                .trail(random.nextBoolean())
                .build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(random.nextInt(FIREWORK_POWER_MAX) + FIREWORK_POWER_OFFSET);
        firework.setFireworkMeta(fireworkMeta);
    }

    private static Color getRandomColor() {
        return Color.fromRGB(random.nextInt(RGB_MAX), random.nextInt(RGB_MAX), random.nextInt(RGB_MAX));
    }

    private static FireworkEffect.Type getRandomType() {
        final var fireworkTypes = FireworkEffect.Type.values();
        return fireworkTypes[random.nextInt(fireworkTypes.length)];
    }
}
