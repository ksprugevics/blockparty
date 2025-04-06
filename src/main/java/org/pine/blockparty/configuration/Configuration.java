package org.pine.blockparty.configuration;

public enum Configuration {

    ARENA_FILE_PATH                   ("arena-file-path", ""),
    STATISTICS_FILE_PATH              ("stats-file-path", "plugins/blockparty/stats.json"),
    WORLD_NAME                        ("world-name", "world"),
    POWER_UP_CHANCE_DENOMINATOR       ("power-up-chance-denominator", "3"),
    SPECIAL_ROUND_CHANCE_DENOMINATOR  ("special-round-chance-denominator", "2"),
    LOBBY_CHECK_INITIAL_DELAY_SECONDS ("lobby-check-initial-delay-seconds", "15"),
    LOBBY_CHECK_INTERVAL_SECONDS      ("lobby-check-interval-seconds", "5"),
    LOBBY_GAME_START_SECONDS          ("lobby-game-start-seconds", "20");

    private final String key;
    private final String defaultValue;

    Configuration(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
