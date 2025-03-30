package org.pine.blockparty.model.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.util.Random;

public enum Music {

    CAT       ("Cat",       "minecraft:music_disc.cat",       0.75F, 1.0f, 3700 + 60),
    BLOCKS    ("Blocks",    "minecraft:music_disc.blocks",    0.75F, 1.0f, 6900 + 60),
    CHIRP     ("Chirp",     "minecraft:music_disc.chirp",     0.75F, 1.0f, 3700 + 60),
    FAR       ("Far",       "minecraft:music_disc.far",       0.75F, 1.0f, 3480 + 60),
    MALL      ("Mall",      "minecraft:music_disc.mall",      0.75F, 1.0f, 3940 + 60),
    MELLOHI   ("Mellohi",   "minecraft:music_disc.mellohi",   0.75F, 1.0f, 1920 + 60),
    STALL     ("Stall",     "minecraft:music_disc.stal",      0.75F, 1.0f, 3000 + 60),
    STRAD     ("Strad",     "minecraft:music_disc.strad",     0.75F, 1.0f, 3760 + 60),
    WARD      ("Ward",      "minecraft:music_disc.ward",      0.75F, 1.0f, 5020 + 60),
    WAIT      ("Wait",      "minecraft:music_disc.wait",      0.75F, 1.0f, 4620 + 60),
    PIGSTEP   ("Pigstep",   "minecraft:music_disc.pigstep",   0.75F, 1.0f, 2960 + 60),
    OTHERSIDE ("Otherside", "minecraft:music_disc.otherside", 0.75F, 1.0f, 3900 + 60),
    RELIC     ("Relic",     "minecraft:music_disc.relic",     0.75F, 1.0f, 4360 + 60);

    private final String title;
    private final Sound sound;
    private final int lengthInTicks;

    @SuppressWarnings("PatternValidation")
    Music(String title, String soundResourceKey, float volume, float pitch, int lengthInTicks) {
        this.title = title;
        this.sound = Sound.sound(Key.key(soundResourceKey), Sound.Source.RECORD, volume, pitch);
        this.lengthInTicks = lengthInTicks;
    }

    public String getTitle() {
        return title;
    }

    public Sound getSound() {
        return sound;
    }

    public int getLengthInTicks() {
        return lengthInTicks;
    }

    public static Music getRandomSong() {
        return Music.values()[new Random().nextInt(Music.values().length)];
    }
}
