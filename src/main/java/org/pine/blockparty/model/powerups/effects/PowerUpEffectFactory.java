package org.pine.blockparty.model.powerups.effects;

import org.pine.blockparty.exceptions.BlockpartyException;

import java.util.List;
import java.util.Random;

public class PowerUpEffectFactory {

    private PowerUpEffectFactory() {
        throw new BlockpartyException();
    }

    private static final List<PowerUpEffect> POWER_UPS = List.of(
            new BlindnessPowerUpEffect(),
            new DarknessPowerUpEffect(),
            new EnderPearlPowerUpEffect(),
            new JumpBoostPowerUpEffect(),
            new LevitationPowerUpEffect(),
            new NauseaPowerUpEffect(),
            new RandomTeleportPowerUpEffect(),
            new SlowFallingPowerUpEffect(),
            new SlownessPowerUpEffect(),
            new SpeedPowerUpEffect(),
            new FishingRodPowerUpEffect(),
            new SecondChancePowerUpEffect()
    );

    private static final Random random = new Random();

    public static PowerUpEffect getRandomPowerUpEffect() {
        return POWER_UPS.get(random.nextInt(POWER_UPS.size()));
    }
}
