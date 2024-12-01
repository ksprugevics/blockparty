package org.pine.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.pine.exceptions.BlockpartyException;

import java.util.Arrays;

import static org.bukkit.Material.*;

public enum XBlock {

    WHITE       (WHITE_CONCRETE,        Component.text("White",         TextColor.color(0xFFFFFF))),
    ORANGE      (ORANGE_CONCRETE,       Component.text("Orange",        TextColor.color(0xD87F33))),
    MAGENTA     (MAGENTA_CONCRETE,      Component.text("Magenta",       TextColor.color(0xB24CD8))),
    LIGHT_BLUE  (LIGHT_BLUE_CONCRETE,   Component.text("Light Blue",    TextColor.color(0x6699D8))),
    YELLOW      (YELLOW_CONCRETE,       Component.text("Yellow",        TextColor.color(0xE5E533))),
    LIME        (LIME_CONCRETE,         Component.text("Lime",          TextColor.color(0x7FCC19))),
    PINK        (PINK_CONCRETE,         Component.text("Pink",          TextColor.color(0xF27FA5))),
    GRAY        (GRAY_CONCRETE,         Component.text("Gray",          TextColor.color(0x4C4C4C))),
    LIGHT_GRAY  (LIGHT_GRAY_CONCRETE,   Component.text("Light Gray",    TextColor.color(0x999999))),
    CYAN        (CYAN_CONCRETE,         Component.text("Cyan",          TextColor.color(0x4C7F99))),
    PURPLE      (PURPLE_CONCRETE,       Component.text("Purple",        TextColor.color(0x7F3FB2))),
    BLUE        (BLUE_CONCRETE,         Component.text("Blue",          TextColor.color(0x334CB2))),
    BROWN       (BROWN_CONCRETE,        Component.text("Brown",         TextColor.color(0x664C33))),
    GREEN       (GREEN_CONCRETE,        Component.text("Green",         TextColor.color(0x667F33))),
    RED         (RED_CONCRETE,          Component.text("Red",           TextColor.color(0x993333))),
    BLACK       (BLACK_CONCRETE,        Component.text("Black",         TextColor.color(0x191919)));

    private final Material material;
    private final Component displayText;

    XBlock(Material material, Component displayText) {
        this.material = material;
        this.displayText = displayText;
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
}
