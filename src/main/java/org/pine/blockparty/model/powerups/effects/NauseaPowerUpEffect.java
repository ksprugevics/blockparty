package org.pine.blockparty.model.powerups.effects;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NauseaPowerUpEffect implements PowerUpEffect {

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 15, 2));
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.7f, 1.0f);
        player.chat("I think I'm gonna be sick!");
    }
}
