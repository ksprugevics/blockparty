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

    public boolean playSoundEffect(SoundEffect sound) {
        for (Player player : gameWorld.getPlayers()) {
            player.playSound(sound.getSound(), player);
        }

        return true;
    }

    public String playMusic() {
        final Music currentSong = Music.getRandomSong();
        for (Player player : gameWorld.getPlayers()) {
            player.playSound(currentSong.getSound(), player);
        }

        return currentSong.getTitle();
    }

    public void stopMusic() {
        for (Player player : gameWorld.getPlayers()) {
            player.stopSound(SoundCategory.RECORDS);
        }
    }
}
