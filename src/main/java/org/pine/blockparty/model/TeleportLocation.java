package org.pine.blockparty.model;

import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public enum TeleportLocation {

    LOBBY                (false, -11.5,  11.5,  16.5,    270f,   0f),
    PLATFORM_CENTER      (false, 16.5,   1,     16.5,    270f,   0f),
    PLATFORM_CENTER_MED  (false, 16.5,   2,     16.5,    270f,   0f),
    PLATFORM_CENTER_HIGH (false, 16.5,   12,     16.5,    270f,   0f),
    PLATFORM_1           (true,  3,      1,     4,       270f,   0f),
    PLATFORM_2           (true,  3,      1,     8,       270f,   0f),
    PLATFORM_3           (true,  3,      1,     12,      270f,   0f),
    PLATFORM_4           (true,  3,      1,     16,      270f,   0f),
    PLATFORM_5           (true,  3,      1,     20,      270f,   0f),
    PLATFORM_6           (true,  3,      1,     24,      270f,   0f),
    PLATFORM_7           (true,  3,      1,     28,      270f,   0f),
    PLATFORM_8           (true,  28,     1,     28,      90f,    0f),
    PLATFORM_9           (true,  28,     1,     24,      90f,    0f),
    PLATFORM_10          (true,  28,     1,     20,      90f,    0f),
    PLATFORM_11          (true,  28,     1,     16,      90f,    0f),
    PLATFORM_12          (true,  28,     1,     12,      90f,    0f),
    PLATFORM_13          (true,  28,     1,     8,       90f,    0f),
    PLATFORM_14          (true,  28,     1,     4,       90f,    0f);

    private final Location location;
    private final boolean isStartingPosition;

    TeleportLocation(boolean isStartingPosition, double x, double y, double z, float yaw, float pitch) {
        this.isStartingPosition = isStartingPosition;
        this.location = new Location(null, x, y, z, yaw, pitch);
    }

    public Location getLocation() {
        return location;
    }

    public static List<TeleportLocation> getStartingLocations() {
        return Arrays.stream(TeleportLocation.values())
                .filter(loc -> loc.isStartingPosition)
                .toList();
    }
}
