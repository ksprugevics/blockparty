package org.pine.blockparty.listeners;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SpecialRoundEventListener implements Listener {

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) {
            return;
        }

        if (event.getHitEntity() instanceof Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 10));
        }
    }

    @EventHandler
    public void onPlayerDamagedByCreeper(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Creeper && event.getEntity() instanceof Player player) {
            event.setCancelled(true);

            Vector knockback = player.getLocation().toVector().subtract(event.getDamager().getLocation().toVector()).normalize().multiply(1.5);
            knockback.setY(1);
            player.setVelocity(knockback);
        }
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper) {
            event.setYield(0);
        }
    }
}
