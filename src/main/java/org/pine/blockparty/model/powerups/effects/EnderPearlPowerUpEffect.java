package org.pine.blockparty.model.powerups.effects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnderPearlPowerUpEffect implements PowerUpEffect {

    @Override
    public void apply(Player player) {
        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
    }
}
