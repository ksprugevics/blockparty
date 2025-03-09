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
import org.bukkit.scoreboard.*;
import org.pine.blockparty.model.Round;
import org.pine.blockparty.model.XBlock;
import org.pine.blockparty.model.sound.SoundEffect;

import java.time.Duration;

import static net.kyori.adventure.text.Component.join;

public class UiManager {

    private static final Component sidebarTitle = Component.text("§e§lBlockparty").decorate(TextDecoration.BOLD);
    private static final Duration fadeIn = Duration.ofSeconds(1L); // TODO again, maybe need to move to duration
    private static final Duration stay = Duration.ofSeconds(3L); // TODO again, maybe need to move to duration
    private static final Duration fadeOut = Duration.ofSeconds(1L); // TODO again, maybe need to move to duration

    private final SoundManager soundManager;
    private final World gameWorld;
    private final Scoreboard scoreboard;
    private final Objective objective;

    private BossBar bossBar;

    public UiManager(World gameWorld, SoundManager soundManager) {
        this.gameWorld = gameWorld;
        this.soundManager = soundManager;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("sidebar", Criteria.create("bpstats"), sidebarTitle);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        initializeDefaultBossbar();
    }

    public void initializeUiForPlayer(Player player, Round currentRound) {
        if (currentRound != null) {
            updateScoreboardRoundInfo(currentRound.getParticipants().size(),
                    currentRound.getDifficulty().getCounter(),
                    currentRound.getDifficulty().getDurationInSecondsLabel());
        }

        showScoreboardToPlayer(player);
        showBossBarToPlayer(player);
    }

    public void initializeDefaultBossbar() {
        this.bossBar = BossBar.bossBar(
                Component.text("§5§lBlockparty"),
                0f,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );
    }

    public void updateBossBar(Component text) {
        this.bossBar.name(text.decorate(TextDecoration.BOLD));
    }

    public void startSplash() {
        broadcastTitle(Component.text("§e§lBlockparty"), Component.text("§lTime to DANCE!"));
    }

    public void broadcastTitle(Component title, Component subtitle) {

        for (Player player : gameWorld.getPlayers()) {
            player.showTitle(Title.title(title, subtitle, Title.Times.times(fadeIn, stay, fadeOut)));
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

    public void colorCountdown(Plugin plugin, Component color, int counter) {

        switch (counter) {
            case 4 -> soundManager.playSoundEffect(SoundEffect.DINK1);
            case 2 -> soundManager.playSoundEffect(SoundEffect.DINK2);
            case 0 -> soundManager.playSoundEffect(SoundEffect.DINK3);
        }

        Component out = join(JoinConfiguration.noSeparators(), Component.text("█ ".repeat(counter / 2)), color, Component.text(" █".repeat(counter / 2))).decorate(TextDecoration.BOLD).color(color.color());
        for (Player player : gameWorld.getPlayers()) {
            player.sendActionBar(out);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            colorCountdown(plugin, color, counter - 1);
        }, 10L);
    }

    public void showScoreboardToPlayer(Player player) {
        player.setScoreboard(scoreboard);
        updateScoreboard(0, 1, "1s", 1, 1);
    }

    public void showBossBarToPlayer(Player player) {
        player.showBossBar(bossBar);
    }

    public void updateScoreboard(int playersLeft, int round, String roundSpeed, int games, int wins) {
        updateLine(9, Component.text("§d§lDancers left"));
        updateLine(8, Component.text(playersLeft).append(Component.text(" Dancers").color(XBlock.LIGHT_GRAY.getDisplayText().color())));
        updateLine(7, Component.empty());
        updateLine(6, Component.text("§b§lGame info"));
        updateLine(5, Component.text("Round: ").color(XBlock.LIGHT_GRAY.getDisplayText().color()).append(Component.text(round).color(XBlock.WHITE.getDisplayText().color()))
                .append(Component.text("/25").color(XBlock.GRAY.getDisplayText().color())));
        updateLine(4, Component.text("Round speed: ").color(XBlock.LIGHT_GRAY.getDisplayText().color()).append(Component.text(roundSpeed).color(XBlock.WHITE.getDisplayText().color())));
        updateLine(3, Component.empty());
        updateLine(2, Component.text("§a§lYour stats"));
        updateLine(1, Component.text("Games: ").color(XBlock.LIGHT_GRAY.getDisplayText().color()).append(Component.text(games).color(XBlock.WHITE.getDisplayText().color())));
        updateLine(0, Component.text("Wins: ").color(XBlock.LIGHT_GRAY.getDisplayText().color()).append(Component.text(wins).color(XBlock.WHITE.getDisplayText().color())));
    }

    public void updateScoreboardRoundInfo(int playersLeft, int round, String roundSpeed) {
        updateLine(8, Component.text(playersLeft).append(Component.text(" Dancers").color(XBlock.LIGHT_GRAY.getDisplayText().color())));
        updateLine(5, Component.text("Round: ").color(XBlock.LIGHT_GRAY.getDisplayText().color()).append(Component.text(round).color(XBlock.WHITE.getDisplayText().color()))
                .append(Component.text("/25").color(XBlock.GRAY.getDisplayText().color())));
        updateLine(4, Component.text("Round speed: ").color(XBlock.LIGHT_GRAY.getDisplayText().color()).append(Component.text(roundSpeed).color(XBlock.WHITE.getDisplayText().color())));
    }

    public void updateScoreboardRoundParticipants(int playersLeft) {
        updateLine(8, Component.text(playersLeft).append(Component.text(" Dancers").color(XBlock.LIGHT_GRAY.getDisplayText().color())));
    }

    private void updateLine(int line, Component text) {
        Team team = scoreboard.getTeam("line" + line);
        if (team == null) {
            team = scoreboard.registerNewTeam("line" + line);
        }
        team.addEntry("§" + line);
        team.prefix(text);
        objective.getScore("§" + line).setScore(line);
    }
}
