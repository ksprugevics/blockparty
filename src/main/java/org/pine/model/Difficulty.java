package org.pine.model;

import org.pine.exceptions.BlockpartyException;

import java.util.Arrays;

public enum Difficulty {

    BLANK   (-1,      "",     0L),
    LVL_1   (1,       "6s",   120L),
    LVL_2   (2,       "5.5s", 110L),
    LVL_3   (3,       "5.5s", 110L),
    LVL_4   (4,       "5s",   100L),
    LVL_5   (5,       "5s",   100L),
    LVL_6   (6,       "4.5s", 90L),
    LVL_7   (7,       "4.5s", 90L),
    LVL_8   (8,       "4s",   80L),
    LVL_9   (9,       "4s",   80L),
    LVL_10  (10,      "3.5s", 70L),
    LVL_11  (11,      "3.5s", 70L),
    LVL_12  (12,      "3s",   60L),
    LVL_13  (13,      "3s",   60L),
    LVL_14  (14,      "2.5s", 50L),
    LVL_15  (15,      "2.5s", 50L),
    LVL_16  (16,      "2s",   40L),
    LVL_17  (17,      "2s",   40L),
    LVL_18  (18,      "1.5s", 30L),
    LVL_19  (19,      "1.5s", 30L),
    LVL_20  (20,      "1s",   20L),
    LVL_21  (21,      "1s",   20L),
    LVL_22  (22,      "0.5s", 10L),
    LVL_23  (23,      "0.5s", 10L),
    LVL_24  (24,      "0.5s", 10L),
    LVL_25  (25,      "0.5s", 10L),
    ;

    private final int level;
    private final String timeInSeconds;
    private final long timeInTicks;

    Difficulty(int level, String timeInSeconds, long timeInTicks) {
        this.level = level;
        this.timeInSeconds = timeInSeconds;
        this.timeInTicks = timeInTicks;
    }

    public int getLevel() {
        return level;
    }

    public String getTimeInSeconds() {
        return timeInSeconds;
    }

    public long getTimeInTicks() {
        return timeInTicks;
    }

    public static Difficulty fromLevel(int level) {
        return Arrays.stream(Difficulty.values())
                .filter(diff -> diff.level == level)
                .findFirst()
                .orElse(BLANK);
    }

    public static Difficulty getNextDifficulty(Difficulty currentDifficulty) {
        return fromLevel(currentDifficulty.getLevel() + 1);
    }
}
