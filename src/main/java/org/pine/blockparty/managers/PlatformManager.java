package org.pine.blockparty.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.pine.blockparty.model.TeleportLocation;

import java.util.List;
import java.util.Random;

public class PlatformManager {

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

    public void launchRandomFirework() {
        Location location =  possibleFireworkLaunchLocations.get(random.nextInt(possibleFireworkLaunchLocations.size()));
        World world = location.getWorld();
        if (world == null) return;

        Firework firework = world.spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        // Random firework effect
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(random.nextBoolean())
                .withColor(getRandomColor(), getRandomColor())
                .withFade(getRandomColor())
                .with(getRandomType())
                .trail(random.nextBoolean())
                .build();

        meta.addEffect(effect);
        meta.setPower(random.nextInt(2) + 1);
        firework.setFireworkMeta(meta);
    }

    private static Color getRandomColor() {
        return Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private static FireworkEffect.Type getRandomType() {
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        return types[random.nextInt(types.length)];
    }
}
