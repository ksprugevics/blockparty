package org.pine.blockparty.managers;

import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.pine.blockparty.model.sound.Music;
import org.pine.blockparty.model.sound.SoundEffect;

public class SoundManager {

    private final World gameWorld;

    public SoundManager(World gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void playSoundEffectForAllPlayers(SoundEffect soundEffect) {
        for (Player player : gameWorld.getPlayers()) {
            player.playSound(soundEffect.getSound(), player);
        }
    }

    public String playRandomSongForAllPlayers() {
        final Music currentSong = Music.getRandomSong();
        for (Player player : gameWorld.getPlayers()) {
            player.playSound(currentSong.getSound(), player);
        }

        return currentSong.getTitle();
    }

    public void stopSoundsForAllPlayers() {
        for (Player player : gameWorld.getPlayers()) {
            player.stopSound(SoundCategory.RECORDS);
        }
    }
}
