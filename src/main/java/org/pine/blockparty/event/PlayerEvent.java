package org.pine.blockparty.event;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.pine.blockparty.managers.GameManager;
import org.pine.blockparty.managers.UiManager;
import org.pine.blockparty.model.Round;

import static org.pine.blockparty.managers.PlayerManager.teleportPlayerToLobby;

public class PlayerEvent implements Listener {

    private static final int FALL_THRESHOLD_Y = -5;
    private final GameManager gameManager;
    private final UiManager uiManager;

    public PlayerEvent(GameManager gameManager, UiManager uiManager) {
        this.gameManager = gameManager;
        this.uiManager = uiManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        teleportPlayerToLobby(player);
        uiManager.showScoreboardToPlayer(player);
        final Round currentRound = gameManager.getCurrentRound();
        if (currentRound != null) {
            uiManager.updateScoreboardRoundInfo(currentRound.getParticipants().size(), currentRound.getDifficulty().getLevel(),
                    currentRound.getDifficulty().getTimeInSeconds());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        gameManager.playerLeft(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location loc = player.getLocation();
        if (loc.getY() < FALL_THRESHOLD_Y) {
            gameManager.playerEliminated(player);
        }
    }
}
