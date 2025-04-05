package org.pine.blockparty.model.specials;

import net.kyori.adventure.text.Component;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static org.pine.blockparty.managers.PlatformManager.X_MAX;
import static org.pine.blockparty.managers.PlatformManager.Z_MAX;

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
        world.setDifficulty(Difficulty.EASY);
        creeper = (Creeper) world.spawnEntity(new Location(world, X_MAX / 2, 2, Z_MAX / 2), EntityType.CREEPER); // todo change to random coord

        creeper.customName(CREEPER_NAME);
        creeper.setCustomNameVisible(true);
        creeper.setExplosionRadius(EXPLOSION_RADIUS);
        creeper.setMaxFuseTicks(FUSE_TIME_TICKS);
        creeper.setRemoveWhenFarAway(false);
        creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) roundDurationInTicks, 1));
        creeper.setPersistent(true);
    }

    @Override
    public void stop(World world, List<Player> player, long roundDurationInTicks, Plugin plugin) {
        world.setDifficulty(Difficulty.PEACEFUL);
        creeper.remove();
    }
}
