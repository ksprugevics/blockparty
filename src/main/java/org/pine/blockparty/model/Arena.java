package org.pine.blockparty.model;

import org.bukkit.Material;

import java.util.List;

public record Arena(String name, boolean enabled, List<Material> uniqueBlocks, Material[][] pattern) {
}
