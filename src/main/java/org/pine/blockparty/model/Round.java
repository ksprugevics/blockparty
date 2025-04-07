package org.pine.blockparty.model;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Round {

    private static final Random random = new Random();

    private final Arena arena;
    private final XBlock xBlock;
    private final List<Player> participants;
    private final Difficulty difficulty;
    private List<Player> eliminations = new ArrayList<>();

    public Round(Arena arena, Difficulty difficulty, List<Player> participants) {
        this.arena = arena;
        this.difficulty = difficulty;
        this.participants = participants;
        this.xBlock = selectXBlock();
    }

    public Arena getArena() {
        return arena;
    }

    public XBlock getxBlock() {
        return xBlock;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<Player> getEliminations() {
        return eliminations;
    }

    private XBlock selectXBlock() {
        final List<Material> levelBlocks = this.arena.uniqueBlocks();
        return XBlock.fromMaterial(levelBlocks.get(random.nextInt(levelBlocks.size())));
    }
}
