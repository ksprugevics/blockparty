package org.pine.blockparty.model.powerups;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.pine.blockparty.model.powerups.effects.PowerUpEffect;
import org.pine.blockparty.model.powerups.effects.PowerUpEffectFactory;

public class PowerUp {

    private final Block block;
    private final PowerUpEffect powerUpEffect;

    public PowerUp(Block block) {
        this.powerUpEffect = PowerUpEffectFactory.getRandomPowerUpEffect();
        this.block = block;
        block.setType(Material.BEACON);
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
}
