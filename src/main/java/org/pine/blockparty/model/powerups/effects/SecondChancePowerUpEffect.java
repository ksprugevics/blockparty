package org.pine.blockparty.model.powerups.effects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SecondChancePowerUpEffect implements PowerUpEffect {

    @Override
    public void apply(Player player) {
        player.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING));
    }
}
