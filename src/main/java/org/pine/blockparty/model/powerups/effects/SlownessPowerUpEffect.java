package org.pine.blockparty.model.powerups.effects;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlownessPowerUpEffect implements PowerUpEffect {

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 1));
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.7f, 1.0f);
        player.chat("I can't move!");
    }
}
