package org.pine.blockparty.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.pine.blockparty.managers.GameManager;
import org.pine.blockparty.managers.PlatformManager;
import org.pine.blockparty.managers.PlayerManager;
import org.pine.blockparty.managers.UiManager;
import org.pine.blockparty.model.powerups.PowerUp;

import java.util.List;

public class PlayerEventListener implements Listener {

    private static final int FALL_THRESHOLD_Y = -5; // TODO: Configuration?

    private final GameManager gameManager;
    private final UiManager uiManager;
    private final PlayerManager playerManager;
    private final PlatformManager platformManager;

    private List<Player> affectedPlayers;

    public PlayerEventListener(GameManager gameManager, UiManager uiManager, PlayerManager playerManager, PlatformManager platformManager) {
        this.gameManager = gameManager;
        this.uiManager = uiManager;
        this.playerManager = playerManager;
        this.platformManager = platformManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        playerManager.initializePlayerOnJoin(player);
        uiManager.initializeUiForPlayer(player, gameManager.getCurrentRound());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        gameManager.handlePlayerLeaving(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (hasPlayerFallen(player.getLocation())) {
            gameManager.handlePlayerEliminated(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().isLeftClick()) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        final PowerUp powerUp = platformManager.getActivePowerUp();
        if (powerUp == null || powerUp.getBlock() == null || !clickedBlock.getLocation().equals(powerUp.getBlock().getLocation())) {
            return;
        }

        final Player player = event.getPlayer();
        powerUp.getPowerUpEffect().apply(player);
        platformManager.resetActivePowerUp();
    }

    private boolean hasPlayerFallen(Location playerLocation) {
        return playerLocation.getY() < FALL_THRESHOLD_Y;
    }
}
