package org.pine.blockparty.model.specials;

import java.util.List;
import java.util.Random;

public class SpecialRoundFactory {

    private static final List<SpecialRound> SPECIAL_ROUNDS = List.of(
            new AcidRainSpecialRound(),
            new ColorRainSpecialRound(),
            new CreeperSpecialRound(),
            new PvpSpecialRound(),
            new RandomTeleportationSpecialRound(),
            new SnowballFightSpecialRound()
    );

    private static final Random random = new Random();

    public static SpecialRound getRandomSpecialRound() {
        return SPECIAL_ROUNDS.get(random.nextInt(SPECIAL_ROUNDS.size()));
    }
}
