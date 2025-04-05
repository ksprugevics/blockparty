package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PvpSpecialRound implements SpecialRound {

    @Override
    public Component getMessage() {
        return Component.text("§c§lIt's a free for all!");
    }

    @Override
    public void start(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        playSound(playersLeft);
        world.setPVP(true);
        for (Player player : playersLeft) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20 * 20, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 20, 1));
        }
    }

    @Override
    public void stop(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        world.setPVP(false);
        for (Player player : playersLeft) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.removePotionEffect(PotionEffectType.DARKNESS);
            player.removePotionEffect(PotionEffectType.NAUSEA);
        }
    }

    private void playSound(List<Player> players) {
        players.stream().forEach(pl -> pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.7f, 1.0f));
    }
}
