package org.pine.blockparty.managers;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;
import org.pine.blockparty.model.XBlock;
import org.pine.blockparty.model.sound.SoundEffect;

import java.time.Duration;
import java.util.Random;

import static net.kyori.adventure.text.Component.join;

public class UiManager {

    private static final World world = Bukkit.getWorld("world");
    private static final Random random = new Random();

    private static final Component sidebarTitle = Component.text("§e§lBlockparty").decorate(TextDecoration.BOLD);
    private static final Duration fadeIn = Duration.ofSeconds(1L); // TODO again, maybe need to move to duration
    private static final Duration stay = Duration.ofSeconds(3L); // TODO again, maybe need to move to duration
    private static final Duration fadeOut = Duration.ofSeconds(1L); // TODO again, maybe need to move to duration

    private final Scoreboard scoreboard;
    private final Objective objective;

    private BossBar bossBar;

    public UiManager() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("sidebar", Criteria.create("bpstats"), sidebarTitle);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        initializeDefaultBossbar();
    }

    public BossBar getBossBar() {
        return bossBar;
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

    public static void startSplash() {
        broadcastTitle(Component.text("§e§lBlockparty"), Component.text("§lTime to DANCE!"));
    }

    public static void broadcastTitle(Component title, Component subtitle) {
        if (world == null) {
            return;
        }

        for (Player player : world.getPlayers()) {
            player.showTitle(Title.title(title, subtitle, Title.Times.times(fadeIn, stay, fadeOut)));
        }
    }

    public static void sendMessageToPlayerInChat(Player player, String msg) {
        player.sendMessage(msg);
    }

    public static void broadcastInChat(String msg) {
        if (world == null) {
            return;
        }

        for (Player player : world.getPlayers()) {
            player.sendMessage(msg);
        }
    }

    public static void broadcastActionBar(Component textComponent) {
        if (world == null) {
            return;
        }

        for (Player player : world.getPlayers()) {
            player.sendActionBar(textComponent.decorate(TextDecoration.BOLD));
        }
    }

    public static void colorCountdown(Plugin plugin, Component color, int counter) {
        if (world == null || counter < 0) {
            return;
        }

        switch (counter) {
            case 4 -> SoundManager.playSoundEffect(SoundEffect.DINK1);
            case 2 -> SoundManager.playSoundEffect(SoundEffect.DINK2);
            case 0 -> SoundManager.playSoundEffect(SoundEffect.DINK3);
        }

        Component out = join(JoinConfiguration.noSeparators(), Component.text("█ ".repeat(counter / 2)), color, Component.text(" █".repeat(counter / 2))).decorate(TextDecoration.BOLD).color(color.color());
        for (Player player : world.getPlayers()) {
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

    public static void launchRandomFirework() {
        Location location = PlayerManager.startingLocations.get(random.nextInt(PlayerManager.startingLocations.size()));
        World world = location.getWorld();
        if (world == null) return;

        Firework firework = world.spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        // Random firework effect
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(random.nextBoolean())
                .withColor(getRandomColor(), getRandomColor())
                .withFade(getRandomColor())
                .with(getRandomType())
                .trail(random.nextBoolean())
                .build();

        meta.addEffect(effect);
        meta.setPower(random.nextInt(2) + 1);
        firework.setFireworkMeta(meta);
    }

    private static Color getRandomColor() {
        return Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private static FireworkEffect.Type getRandomType() {
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        return types[random.nextInt(types.length)];
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
