package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Difficulty;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.pine.blockparty.managers.PlatformManager;

import java.util.List;

import static org.pine.blockparty.managers.PlatformManager.Y_LVL;

public class CreeperSpecialRound implements SpecialRound {

    private static final Component CREEPER_NAME = Component.text("Buddy");
    private static final int EXPLOSION_RADIUS = 7;
    private static final int FUSE_TIME_TICKS = 30;

    private Creeper creeper;

    @Override
    public Component getMessage() {
        return Component.text("§a§lEww! What is that!?");
    }

    @Override
    public void start(World world, List<Player> playersLeft, long roundDurationInTicks, Plugin plugin) {
        playSound(playersLeft);
        world.setDifficulty(Difficulty.EASY);
        creeper = (Creeper) world.spawnEntity(PlatformManager.randomLocationOnPlatform(Y_LVL + 2, world), EntityType.CREEPER);
        creeper.customName(CREEPER_NAME);
        creeper.setCustomNameVisible(true);
        creeper.setExplosionRadius(EXPLOSION_RADIUS);
        creeper.setMaxFuseTicks(FUSE_TIME_TICKS);
        creeper.setRemoveWhenFarAway(false);
        creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
        creeper.setPersistent(true);
    }

    @Override
    public void stop(World world, List<Player> player, long roundDurationInTicks, Plugin plugin) {
        world.setDifficulty(Difficulty.PEACEFUL);
        creeper.remove();
    }

    private void playSound(List<Player> players) {
        players.stream().forEach(pl -> pl.playSound(pl.getLocation(), Sound.ENTITY_CREEPER_DEATH, 0.7f, 1.0f));
    }
}
