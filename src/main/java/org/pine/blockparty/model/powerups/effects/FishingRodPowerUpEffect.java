package org.pine.blockparty.model.powerups.effects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FishingRodPowerUpEffect implements PowerUpEffect {

    @Override
    public void apply(Player player) {
        player.getInventory().addItem(getOneUseFishingRod());
    }

    public static ItemStack getOneUseFishingRod() {
        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        rod.setDurability((short) (rod.getType().getMaxDurability() - 1));

        ItemMeta meta = rod.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(false); // Ensure it isn't unbreakable
            rod.setItemMeta(meta);
        }

        return rod;
    }
}
