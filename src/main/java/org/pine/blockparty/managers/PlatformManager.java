package org.pine.blockparty.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class PlatformManager {

    private static final short X_MIN = 0;
    private static final short X_MAX = 31;
    private static final short Z_MIN = 0;
    private static final short Z_MAX = 31;
    private static final short Y_LVL = 0;

    // Todo has to be static because methods are static, but does it make sens that it IS static???
    private static final World world = Bukkit.getWorld("world");

    public static void platformToPattern(Material[][] pattern) {
        if (world == null) {
            return;
        }

        for (int i = X_MIN; i <= X_MAX; i++) {
            for (int j = Z_MIN; j <= Z_MAX; j++) {
                world.getBlockAt(i, Y_LVL, j).setType(pattern[Z_MAX - i][j]);
            }
        }
    }

    public static void platformRemoveXBlock(Material xBlock) {
        if (world == null) {
            return;
        }

        for (int i = X_MIN; i <= X_MAX; i++) {
            for (int j = Z_MIN; j <= Z_MAX; j++) {
                Block block = world.getBlockAt(i, Y_LVL, j);
                if (block.getType() != xBlock) {
                    block.setType(Material.AIR);
                }
            }
        }
    }
}
