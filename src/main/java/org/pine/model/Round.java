package org.pine.model;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class Round {

    private Level level;
    private XBlock xBlock;
    private List<Player> participants;

    public Round(Level level, List<Player> participants) {
        this.level = level;
        this.participants = participants;
        this.xBlock = selectXBlock();
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

    private XBlock selectXBlock() {
        final Random random = new Random();
        final List<Material> levelBlocks = level.getBlocks();
        return XBlock.fromMaterial(levelBlocks.get(random.nextInt(levelBlocks.size())));
    }
}
