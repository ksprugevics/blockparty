package org.pine.blockparty.managers;

import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.pine.blockparty.model.sound.Music;
import org.pine.blockparty.model.sound.SoundEffect;

public class SoundManager {

    private static final World world = Bukkit.getWorld("world");

    public static boolean playSoundEffect(SoundEffect sound) {
        if (world == null) {
            return false;
        }

        for (Player player : world.getPlayers()) {
            player.playSound(sound.getSound(), player);
        }

        return true;
    }

    public static void playMusic() {
        if (world == null) {
            return;
        }

        final Music currentSong = Music.getRandomSong();
        for (Player player : world.getPlayers()) {
            player.playSound(currentSong.getSound(), player);
        }

        UiManager.broadcastInChat("§5§lLet's party! Now playing: " + currentSong.getTitle());
    }

    public static void stopMusic() {
        if (world == null) {
            return;
        }

        for (Player player : world.getPlayers()) {
            player.stopSound(SoundCategory.RECORDS);
        }
    }
}
