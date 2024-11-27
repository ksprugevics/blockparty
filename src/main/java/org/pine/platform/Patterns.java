package org.pine.platform;

import org.bukkit.Material;

import java.util.Random;

// Some AI generated patterns
public class Patterns {

    private static final Material[] COLORED_BLOCKS = {
            Material.RED_CONCRETE,
            Material.BLUE_CONCRETE,
            Material.GREEN_CONCRETE,
            Material.YELLOW_CONCRETE,
            Material.PURPLE_CONCRETE
    };

    // Checkerboard Pattern
    public static final Material[][] CHECKERBOARD = new Material[32][32];
    static {
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                CHECKERBOARD[row][col] = COLORED_BLOCKS[(row + col) % 5];
            }
        }
    }

    // Spiral Pattern
    public static final Material[][] SPIRAL = new Material[32][32];
    static {
        int centerX = 16;
        int centerY = 16;
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                double angle = Math.atan2(row - centerY, col - centerX);
                double distance = Math.sqrt(
                        Math.pow(row - centerY, 2) +
                                Math.pow(col - centerX, 2)
                );
                int index = (int)((angle + Math.PI) / (Math.PI * 2) * 5 + distance / 6.4) % 5;
                SPIRAL[row][col] = COLORED_BLOCKS[Math.abs(index)];
            }
        }
    }

    // Wave Pattern
    public static final Material[][] WAVE = new Material[32][32];
    static {
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                int waveIndex = (int)(
                        Math.sin(col * 0.2) * 2 +
                                Math.cos(row * 0.2) * 2 + 2
                ) % 5;
                WAVE[row][col] = COLORED_BLOCKS[Math.abs(waveIndex)];
            }
        }
    }

    // Gradient Pattern
    public static final Material[][] GRADIENT = new Material[32][32];
    static {
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                int index = (row / 6 + col / 6) % 5;
                GRADIENT[row][col] = COLORED_BLOCKS[Math.abs(index)];
            }
        }
    }

    // Radial Pattern
    public static final Material[][] RADIAL = new Material[32][32];
    static {
        int centerX = 16;
        int centerY = 16;
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                double distance = Math.sqrt(
                        Math.pow(row - centerY, 2) +
                                Math.pow(col - centerX, 2)
                );
                int index = (int)(distance / 6.4) % 5;
                RADIAL[row][col] = COLORED_BLOCKS[Math.abs(index)];
            }
        }
    }

    // Triangular Pattern
    public static final Material[][] TRIANGULAR = new Material[32][32];
    static {
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                int triangleIndex = (row + col + (row * col / 10)) % 5;
                TRIANGULAR[row][col] = COLORED_BLOCKS[Math.abs(triangleIndex)];
            }
        }
    }

    // Fractal-like Pattern
    public static final Material[][] FRACTAL = new Material[32][32];
    static {
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                int fractalIndex = (int)(
                        Math.abs(Math.sin(row * 0.1) * col) +
                                Math.abs(Math.cos(col * 0.1) * row)
                ) % 5;
                FRACTAL[row][col] = COLORED_BLOCKS[fractalIndex];
            }
        }
    }

    // Diagonal Stripes Pattern
    public static final Material[][] DIAGONAL_STRIPES = new Material[32][32];
    static {
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                int stripeIndex = (row + col * 2) % 5;
                DIAGONAL_STRIPES[row][col] = COLORED_BLOCKS[Math.abs(stripeIndex)];
            }
        }
    }

    // Cellular Automata-inspired Pattern
    public static final Material[][] CELLULAR = new Material[32][32];
    static {
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                int cellIndex = (row * col + row + col) % 5;
                CELLULAR[row][col] = COLORED_BLOCKS[Math.abs(cellIndex)];
            }
        }
    }

    // Noise-like Pattern
    public static final Material[][] NOISE = new Material[32][32];
    static {
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                int noiseIndex = (int)(
                        Math.abs(Math.sin(row * 0.2) * Math.cos(col * 0.2)) * 10
                ) % 5;
                NOISE[row][col] = COLORED_BLOCKS[Math.abs(noiseIndex)];
            }
        }
    }

    // Utility method to get a random pattern
    public static Material[][] getRandomPattern() {
        Material[][][] allPatterns = {
                CHECKERBOARD,
                SPIRAL,
                WAVE,
                GRADIENT,
                RADIAL,
                TRIANGULAR,
                FRACTAL,
                DIAGONAL_STRIPES,
                CELLULAR,
                NOISE
        };

        return allPatterns[new Random().nextInt(allPatterns.length)];
    }
}
