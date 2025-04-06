package org.pine.blockparty.managers;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.pine.blockparty.model.Round;
import org.pine.blockparty.model.XBlock;
import org.pine.blockparty.model.sound.SoundEffect;

import java.time.Duration;

import static net.kyori.adventure.text.Component.join;

public class UiManager {

    private static final long COLOR_BAR_INTERVAL_TICKS = 10L;

    private static final Duration titleFadeInDuration = Duration.ofSeconds(1L);
    private static final Duration titlePresentDuration = Duration.ofSeconds(3L);
    private static final Duration titleFadeOutDuration = Duration.ofSeconds(1L);

    private final SoundManager soundManager;
    private final World gameWorld;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final BossBar bossBar;

    public UiManager(World gameWorld, SoundManager soundManager) {
        this.gameWorld = gameWorld;
        this.soundManager = soundManager;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = initializeScoreboardObjective();
        this.bossBar = initializeDefaultBossbar();
    }

    public void initializeUiForPlayer(Player player, Round currentRound) {
        if (currentRound != null) {
            updateScoreboardRoundParticipants(currentRound.getParticipants().size());
            updateScoreboardRoundInfo(currentRound.getDifficulty().getCounter(), currentRound.getDifficulty().getDurationInSecondsLabel());
        }

        showScoreboardToPlayer(player);
        showBossBarToPlayer(player);
    }

    public void updateBossBar(Component text) {
        this.bossBar.name(text.decorate(TextDecoration.BOLD));
    }

    public void broadcastStartScreen() {
        broadcastTitle(Component.text("§e§lBlockparty"), Component.text("§lTime to DANCE!"));
    }

    public void broadcastTitle(Component title, Component subtitle) {
        for (Player player : gameWorld.getPlayers()) {
            player.showTitle(Title.title(title, subtitle, Title.Times.times(titleFadeInDuration, titlePresentDuration, titleFadeOutDuration)));
        }
    }

    public void sendMessageToPlayerInChat(Player player, String msg) {
        player.sendMessage(msg);
    }

    public void broadcastInChat(String msg) {
        for (Player player : gameWorld.getPlayers()) {
            player.sendMessage(msg);
        }
    }

    public void broadcastActionBar(Component textComponent) {
        for (Player player : gameWorld.getPlayers()) {
            player.sendActionBar(textComponent.decorate(TextDecoration.BOLD));
        }
    }

    public void colorCountdown(int counter, Component xBlockColorLabel, Plugin plugin) {
        if (counter < 0) {
            return;
        }

        switch (counter) {
            case 4 -> soundManager.playSoundEffectForAllPlayers(SoundEffect.DINK1);
            case 2 -> soundManager.playSoundEffectForAllPlayers(SoundEffect.DINK2);
            case 0 -> soundManager.playSoundEffectForAllPlayers(SoundEffect.DINK3);
        }

        final Component actionBarText = join(JoinConfiguration.noSeparators(),
                Component.text("█ ".repeat(counter / 2)), xBlockColorLabel,
                Component.text(" █".repeat(counter / 2)))
                .decorate(TextDecoration.BOLD).color(xBlockColorLabel.color());

        for (Player player : gameWorld.getPlayers()) {
            player.sendActionBar(actionBarText);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            colorCountdown(counter - 1, xBlockColorLabel, plugin);
        }, COLOR_BAR_INTERVAL_TICKS);
    }

    public void updateScoreboardEntire(int playersLeft, int round, String roundSpeed, String songPlaying) {
        updateScoreboardRoundParticipants(playersLeft);
        updateScoreboardRoundInfo(round, roundSpeed);
        updateSongPlaying(songPlaying);
    }

    public void updateScoreboardRoundParticipants(int playersLeft) {
        updateScoreboardLine(9, Component.text("§d§lDancers left"));
        updateScoreboardLine(8, Component.text(playersLeft).append(Component.text(" Dancers").color(XBlock.LIGHT_GRAY.getDisplayText().color())));
    }

    public void updateScoreboardRoundInfo(int round, String roundSpeed) {
        updateScoreboardLine(7, Component.empty());
        updateScoreboardLine(6, Component.text("§b§lGame info"));
        updateScoreboardLine(5, Component.text("Round: ").color(XBlock.LIGHT_GRAY.getDisplayText().color()).append(Component.text(round).color(XBlock.WHITE.getDisplayText().color()))
                .append(Component.text("/25").color(XBlock.GRAY.getDisplayText().color())));
        updateScoreboardLine(4, Component.text("Round speed: ").color(XBlock.LIGHT_GRAY.getDisplayText().color()).append(Component.text(roundSpeed).color(XBlock.WHITE.getDisplayText().color())));
        updateScoreboardLine(3, Component.empty());
    }

    public void updateSongPlaying(String songPlaying) {
        updateScoreboardLine(2, Component.text("§a§lMusic"));
        updateScoreboardLine(1, Component.text("Now dancing to:").color(XBlock.LIGHT_GRAY.getDisplayText().color()));
        updateScoreboardLine(0, Component.text(songPlaying).color(XBlock.WHITE.getDisplayText().color()));
    }

    private Objective initializeScoreboardObjective() {
        final Objective scoreboardObjective = this.scoreboard.registerNewObjective("sidebar", Criteria.create("bpstats"),
                Component.text("§e§lBlockparty").decorate(TextDecoration.BOLD));
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return scoreboardObjective;
    }

    private BossBar initializeDefaultBossbar() {
        return BossBar.bossBar(
                Component.text("§5§lBlockparty"),
                0f,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );
    }

    private void showScoreboardToPlayer(Player player) {
        player.setScoreboard(scoreboard);
        updateScoreboardEntire(0, 1, "1s", "");
    }

    private void showBossBarToPlayer(Player player) {
        player.showBossBar(bossBar);
    }

    private void updateScoreboardLine(int lineNumber, Component text) {
        Team team = scoreboard.getTeam("line" + lineNumber);
        if (team == null) {
            team = scoreboard.registerNewTeam("line" + lineNumber);
        }
        team.addEntry("§" + lineNumber);
        team.prefix(text);
        objective.getScore("§" + lineNumber).setScore(lineNumber);
    }
}
