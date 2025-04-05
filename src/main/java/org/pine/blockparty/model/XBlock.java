package org.pine.blockparty.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.pine.blockparty.exceptions.BlockpartyException;

import java.util.Arrays;
import java.util.Random;

import static org.bukkit.Material.*;

public enum XBlock {

    WHITE       (WHITE_CONCRETE,      "White",      0xFFFFFF),
    ORANGE      (ORANGE_CONCRETE,     "Orange",     0xD87F33),
    MAGENTA     (MAGENTA_CONCRETE,    "Magenta",    0xB24CD8),
    LIGHT_BLUE  (LIGHT_BLUE_CONCRETE, "Light Blue", 0x6699D8),
    YELLOW      (YELLOW_CONCRETE,     "Yellow",     0xE5E533),
    LIME        (LIME_CONCRETE,       "Lime",       0x7FCC19),
    PINK        (PINK_CONCRETE,       "Pink",       0xF27FA5),
    GRAY        (GRAY_CONCRETE,       "Gray",       0x4C4C4C),
    LIGHT_GRAY  (LIGHT_GRAY_CONCRETE, "Light Gray", 0x999999),
    CYAN        (CYAN_CONCRETE,       "Cyan",       0x4C7F99),
    PURPLE      (PURPLE_CONCRETE,     "Purple",     0x7F3FB2),
    BLUE        (BLUE_CONCRETE,       "Blue",       0x334CB2),
    BROWN       (BROWN_CONCRETE,      "Brown",      0x664C33),
    GREEN       (GREEN_CONCRETE,      "Green",      0x667F33),
    RED         (RED_CONCRETE,        "Red",        0x993333),
    BLACK       (BLACK_CONCRETE,      "Black",      0x191919);

    private static final Random random = new Random();
    private final Material material;
    private final Component displayText;

    XBlock(Material material, String labelText, int labelColor) {
        this.material = material;
        this.displayText = Component.text(labelText, TextColor.color(labelColor));
    }

    public Material getMaterial() {
        return material;
    }

    public Component getDisplayText() {
        return displayText;
    }

    public static XBlock fromMaterial(Material material) {
        return Arrays.stream(XBlock.values())
                .filter(block -> block.getMaterial() == material)
                .findFirst()
                .orElseThrow(() -> new BlockpartyException("No XBlock found for material: " + material));
    }

    public static XBlock randomBlock() {
        return values()[random.nextInt(values().length)];
    }
}
