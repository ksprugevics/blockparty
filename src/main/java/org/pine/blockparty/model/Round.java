package org.pine.blockparty.model;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Round {

    private final Level level;
    private final XBlock xBlock;
    private final List<Player> participants;
    private Difficulty difficulty;
    private List<Player> eliminations;

    public Round(Level level, Difficulty difficulty, List<Player> participants) {
        this.level = level;
        this.difficulty = difficulty;
        this.participants = participants;
        this.xBlock = selectXBlock();
        this.eliminations = new ArrayList<>();
    }

    public Level getLevel() {
        return level;
    }

    public XBlock getxBlock() {
        return xBlock;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public List<Player> getEliminations() {
        return eliminations;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    private XBlock selectXBlock() {
        final Random random = new Random();
        final List<Material> levelBlocks = level.getBlocks();
        return XBlock.fromMaterial(levelBlocks.get(random.nextInt(levelBlocks.size())));
    }
}
