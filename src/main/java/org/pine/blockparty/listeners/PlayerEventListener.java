package org.pine.blockparty.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.pine.blockparty.managers.GameManager;
import org.pine.blockparty.managers.PlatformManager;
import org.pine.blockparty.managers.PlayerManager;
import org.pine.blockparty.managers.UiManager;
import org.pine.blockparty.model.TeleportLocation;
import org.pine.blockparty.model.powerups.PowerUp;

import static org.pine.blockparty.managers.PlatformManager.FALL_THRESHOLD_Y;

public class PlayerEventListener implements Listener {

    private final GameManager gameManager;
    private final UiManager uiManager;
    private final PlayerManager playerManager;
    private final PlatformManager platformManager;

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
        uiManager.sendMessageToPlayerInChat(player, "Welcome to Blockparty! Type /bphelp for more information.");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        gameManager.handlePlayerLeaving(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (hasPlayerFallen(player.getLocation())) {
            if (playerHasSecondChancePowerUp(player)) {
                handlePlayerSecondChance(player);
                return;
            }

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
        if (playerLocation == null) {
            return false;
        }

        return playerLocation.getY() < FALL_THRESHOLD_Y;
    }

    private boolean playerHasSecondChancePowerUp(Player player) {
        final PlayerInventory inventory = player.getInventory();

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType() == Material.TOTEM_OF_UNDYING && itemStack.getAmount() >= 1) {
                return true;
            }
        }

        return false;
    }

    private void handlePlayerSecondChance(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 7, 1));
        playerManager.teleportPlayer(TeleportLocation.PLATFORM_CENTER_HIGH, player);
        playerManager.removeItemFromPlayerInventory(player, Material.TOTEM_OF_UNDYING);
    }
}
