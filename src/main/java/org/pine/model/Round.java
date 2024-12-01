package org.pine.model;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class Round {

    private Level level;
    private Material xBlock;
    private List<Player> participants;

    public Round(Level level, List<Player> participants) {
        this.level = level;
        this.participants = participants;
        this.xBlock = selectXBlock();
    }

    public Level getLevel() {
        return level;
    }

    public Material getxBlock() {
        return xBlock;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    private Material selectXBlock() {
        final Random random = new Random();
        final List<Material> levelBlocks = level.getBlocks();
        return levelBlocks.get(random.nextInt(levelBlocks.size()));
    }
}
