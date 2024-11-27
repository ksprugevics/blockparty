package org.pine.platform;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Platform {

    private static final short X_MIN = 0;
    private static final short X_MAX = 32;
    private static final short Z_MIN = 0;
    private static final short Z_MAX = 32;
    private static final short Y = 0;

    private static final World world = Bukkit.getWorld("world");

    // Change the platform to the default pattern
    public static void resetPlatform() {
        platformToPattern(Patterns.getRandomPattern());
    }

    // Change the platform to the specific pattern
    public static void platformToPattern(Material[][] pattern) {
        for (int i = X_MIN; i < X_MAX; i++) {
            for (int j = Z_MIN; j < Z_MAX; j++) {
                world.getBlockAt(i, Y, j).setType(pattern[i][j]); // todo account for NPE
            }
        }
    }

    // Fill the platform with a block
    public static void platformToBlock(Material xBlock) {
        for (int i = X_MIN; i <= X_MAX; i++) {
            for (int j = Z_MIN; j <= Z_MAX; j++) {
                world.getBlockAt(i, Y, j).setType(xBlock); // todo account for NPE
            }
        }
    }

    // Remove all the blocks except for the xBlock
    public static void platformXBlock(Material xBlock) {
        for (int i = X_MIN; i <= X_MAX; i++) {
            for (int j = Z_MIN; j <= Z_MAX; j++) {
                Block block = world.getBlockAt(i, Y, j); // todo account for NPE
                if (block.getType() != xBlock) {
                    block.setType(Material.AIR);
                }
            }
        }
    }

}
