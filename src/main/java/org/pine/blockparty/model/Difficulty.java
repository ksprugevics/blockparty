package org.pine.blockparty.model;

public enum Difficulty {

    DIFFICULTY_0  (0L,   ""),
    DIFFICULTY_1  (120L, "6s"),
    DIFFICULTY_2  (110L, "5.5s"),
    DIFFICULTY_3  (110L, "5.5s"),
    DIFFICULTY_4  (100L, "5s"),
    DIFFICULTY_5  (100L, "5s"),
    DIFFICULTY_6  (90L,  "4.5s"),
    DIFFICULTY_7  (90L,  "4.5s"),
    DIFFICULTY_8  (80L,  "4s"),
    DIFFICULTY_9  (80L,  "4s"),
    DIFFICULTY_10 (70L,  "3.5s"),
    DIFFICULTY_11 (70L,  "3.5s"),
    DIFFICULTY_12 (60L,  "3s"),
    DIFFICULTY_13 (60L,  "3s"),
    DIFFICULTY_14 (50L,  "2.5s"),
    DIFFICULTY_15 (50L,  "2.5s"),
    DIFFICULTY_16 (40L,  "2s"),
    DIFFICULTY_17 (40L,  "2s"),
    DIFFICULTY_18 (30L,  "1.5s"),
    DIFFICULTY_19 (30L,  "1.5s"),
    DIFFICULTY_20 (20L,  "1s"),
    DIFFICULTY_21 (20L,  "1s"),
    DIFFICULTY_22 (10L,  "0.5s"),
    DIFFICULTY_23 (10L,  "0.5s"),
    DIFFICULTY_24 (10L,  "0.5s"),
    DIFFICULTY_25 (10L,  "0.5s");

    private final long durationInTicks;
    private final String durationInSecondsLabel;

    Difficulty(long durationInTicks, String durationInSecondsLabel) {
        this.durationInTicks = durationInTicks;
        this.durationInSecondsLabel = durationInSecondsLabel;
    }

    public int getCounter() {
        return this.ordinal();
    }

    public String getDurationInSecondsLabel() {
        return durationInSecondsLabel;
    }

    public long getDurationInTicks() {
        return durationInTicks;
    }

    public static Difficulty fromCounter(int counter) {
        final Difficulty[] values = Difficulty.values();
        return (counter >= 0 && counter < values.length) ? values[counter] : values[0];
    }

    public static Difficulty getNextDifficulty(Difficulty currentDifficulty) {
        return fromCounter(currentDifficulty.ordinal() + 1);
    }
}
