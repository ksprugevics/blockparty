package org.pine.blockparty.model.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.util.Random;

public enum Music {

    CAT       ("Cat",       "minecraft:music_disc.cat",       0.75F, 1.0f),
    BLOCKS    ("Blocks",    "minecraft:music_disc.blocks",    0.75F, 1.0f),
    CHIRP     ("Chirp",     "minecraft:music_disc.chirp",     0.75F, 1.0f),
    FAR       ("Far",       "minecraft:music_disc.far",       0.75F, 1.0f),
    MALL      ("Mall",      "minecraft:music_disc.mall",      0.75F, 1.0f),
    MELLOHI   ("Mellohi",   "minecraft:music_disc.mellohi",   0.75F, 1.0f),
    STALL     ("Stall",     "minecraft:music_disc.stal",      0.75F, 1.0f),
    STRAD     ("Strad",     "minecraft:music_disc.strad",     0.75F, 1.0f),
    WARD      ("Ward",      "minecraft:music_disc.ward",      0.75F, 1.0f),
    WAIT      ("Wait",      "minecraft:music_disc.wait",      0.75F, 1.0f),
    PIGSTEP   ("Pigstep",   "minecraft:music_disc.pigstep",   0.75F, 1.0f),
    OTHERSIDE ("Otherside", "minecraft:music_disc.otherside", 0.75F, 1.0f),
    RELIC     ("Relic",     "minecraft:music_disc.relic",     0.75F, 1.0f);

    private final String title;
    private final Sound sound;

    @SuppressWarnings("PatternValidation")
    Music(String title, String soundResourceKey, float volume, float pitch) {
        this.title = title;
        this.sound = Sound.sound(Key.key(soundResourceKey), Sound.Source.RECORD, volume, pitch);
    }

    public String getTitle() {
        return title;
    }

    public Sound getSound() {
        return sound;
    }

    public static Music getRandomSong() {
        return Music.values()[new Random().nextInt(Music.values().length)];
    }
}
