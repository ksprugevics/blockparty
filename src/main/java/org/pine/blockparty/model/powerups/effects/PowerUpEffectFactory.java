package org.pine.blockparty.model.powerups.effects;

import java.util.List;
import java.util.Random;

public class PowerUpEffectFactory {

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
            new SpeedPowerUpEffect()
    );

    private static final Random random = new Random();

    public static PowerUpEffect getRandomPowerUpEffect() {
        return POWER_UPS.get(random.nextInt(POWER_UPS.size()));
    }
}
