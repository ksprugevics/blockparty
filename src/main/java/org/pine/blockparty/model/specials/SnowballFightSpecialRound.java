package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class SnowballFightSpecialRound implements SpecialRound {

    @Override
    public Component getMessage() {
        return Component.text("§b§lSnowball fight!");
    }

    @Override
    public void start(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        playSound(playersLeft);
        for (Player player : playersLeft) {
            player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 5));
        }
    }

    @Override
    public void stop(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        // no logic
    }

    private void playSound(List<Player> players) {
        players.stream().forEach(pl -> pl.playSound(pl.getLocation(), Sound.BLOCK_SNOW_STEP, 0.7f, 1.0f));
    }
}
