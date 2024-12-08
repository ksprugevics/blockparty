package org.pine.blockparty.model;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.util.Random;

public enum SoundEffect {
    DINK1("dink1", false, Sound.sound(Key.key("minecraft:block.note_block.harp"), Sound.Source.MASTER, 1f, 1f)),
    DINK2("dink2", false, Sound.sound(Key.key("minecraft:block.note_block.harp"), Sound.Source.MASTER, 1f, 0.8f)),
    DINK3("dink3", false, Sound.sound(Key.key("minecraft:block.note_block.harp"), Sound.Source.MASTER, 1f, 0.6f)),
    CAT("Cat", true,Sound.sound(Key.key("minecraft:music_disc.cat"), Sound.Source.RECORD, 0.75F, 1.0f)),
    BLOCKS("Blocks", true,Sound.sound(Key.key("minecraft:music_disc.blocks"), Sound.Source.RECORD, 0.75F, 1.0f)),
    CHIRP("Chirp", true,Sound.sound(Key.key("minecraft:music_disc.chirp"), Sound.Source.RECORD, 0.75F, 1.0f)),
    FAR("Far", true,Sound.sound(Key.key("minecraft:music_disc.far"), Sound.Source.RECORD, 0.75F, 1.0f)),
    MALL("Mall", true,Sound.sound(Key.key("minecraft:music_disc.mall"), Sound.Source.RECORD, 0.75F, 1.0f)),
    MELLOHI("Mellohi", true,Sound.sound(Key.key("minecraft:music_disc.mellohi"), Sound.Source.RECORD, 0.75F, 1.0f)),
    STALL("Stall", true,Sound.sound(Key.key("minecraft:music_disc.stal"), Sound.Source.RECORD, 0.75F, 1.0f)),
    STRAD("Strad", true,Sound.sound(Key.key("minecraft:music_disc.strad"), Sound.Source.RECORD, 0.75F, 1.0f)),
    WARD("Ward", true,Sound.sound(Key.key("minecraft:music_disc.ward"), Sound.Source.RECORD, 0.75F, 1.0f)),
    WAIT("Wait", true,Sound.sound(Key.key("minecraft:music_disc.wait"), Sound.Source.RECORD, 0.75F, 1.0f)),
    PIGSTEP("Pigstep", true,Sound.sound(Key.key("minecraft:music_disc.pigstep"), Sound.Source.RECORD, 0.75F, 1.0f)),
    OTHERSIDE("Otherside", true,Sound.sound(Key.key("minecraft:music_disc.otherside"), Sound.Source.RECORD, 0.75F, 1.0f)),
    RELIC("Relic", true,Sound.sound(Key.key("minecraft:music_disc.relic"), Sound.Source.RECORD, 0.75F, 1.0f))
    ;

    private final String title;
    private final boolean isMusic;
    private final Sound sound;

    SoundEffect(String name, boolean isMusic, Sound sound) {
        this.title = name;
        this.isMusic = isMusic;
        this.sound = sound;
    }

    public String getTitle() {
        return title;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public Sound getSound() {
        return sound;
    }

    public static SoundEffect getRandomSong() {
        return SoundEffect.values()[new Random().nextInt(2, SoundEffect.values().length)];
    }
}
