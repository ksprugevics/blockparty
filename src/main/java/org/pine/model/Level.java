package org.pine.model;

import org.bukkit.Material;

import java.util.List;

public class Level {

    private final String name;
    private final boolean enable;
    private final List<Material> blocks;
    private final Material[][] pattern;

    public Level(String name, boolean enable, List<Material> blocks, Material[][] pattern) {
        this.name = name;
        this.enable = enable;
        this.blocks = blocks;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public boolean isEnable() {
        return enable;
    }

    public List<Material> getBlocks() {
        return blocks;
    }

    public Material[][] getPattern() {
        return pattern;
    }
}
