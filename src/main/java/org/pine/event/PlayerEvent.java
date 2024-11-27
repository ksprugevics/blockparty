package org.pine.event;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerEvent implements Listener {

    private static final Logger log = LoggerFactory.getLogger(PlayerEvent.class);
    private static final Location lobbyLocation = new Location(Bukkit.getWorld("world"), -11.5, 11.5, 16.5, 270f, 0f);

    public void teleportPlayerToLobby(Player player) {
        player.teleport(lobbyLocation);
        log.info("Teleported {} to lobby", player.getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        teleportPlayerToLobby(player);
    }
}
