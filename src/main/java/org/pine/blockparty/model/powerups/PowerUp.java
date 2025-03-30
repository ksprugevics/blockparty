package org.pine.blockparty.model.powerups;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.pine.blockparty.configuration.Configuration;
import org.pine.blockparty.model.powerups.effects.PowerUpEffect;
import org.pine.blockparty.model.powerups.effects.PowerUpEffectFactory;

public class PowerUp {

    private final Block block;
    private final PowerUpEffect powerUpEffect;

    public PowerUp(Block block) {
        this.powerUpEffect = PowerUpEffectFactory.getRandomPowerUpEffect();
        this.block = block;
        block.setType(Material.BEACON);
        spawnParticles(block);
    }

    public void remove() {
        this.block.setType(Material.AIR);
    }

    public Block getBlock() {
        return block;
    }

    public PowerUpEffect getPowerUpEffect() {
        return powerUpEffect;
    }

    private void spawnParticles(Block block) {
        final World world = Bukkit.getWorld(Configuration.WORLD_NAME.getDefaultValue());
        for (int i = 0; i < 30; i++) {
            final Location particleLocation = block.getLocation().clone().add(0.5, i * 0.1 + 1, 0.5);
            world.spawnParticle(Particle.HAPPY_VILLAGER, particleLocation, 1, 0, 0, 0, 0.1);
        }
    }
}
