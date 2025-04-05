package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface SpecialRound {

    Component getMessage();
    void start(World world, List<Player> playersLeft, long roundDurationTicks, Plugin plugin);
    void stop(World world, List<Player> playersLeft, long roundDurationTicks, Plugin plugin);
}
