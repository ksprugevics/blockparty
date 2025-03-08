package org.pine.blockparty.model.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public enum SoundEffect {

    DINK1("minecraft:block.note_block.harp", 1f, 1f),
    DINK2("minecraft:block.note_block.harp", 1f, 0.8f),
    DINK3("minecraft:block.note_block.harp", 1f, 0.6f);

    private final Sound sound;

    @SuppressWarnings("PatternValidation")
    SoundEffect(String soundResourceKey, float volume, float pitch) {
        this.sound = Sound.sound(Key.key(soundResourceKey), Sound.Source.MASTER, volume, pitch);
    }

    public Sound getSound() {
        return sound;
    }
}
