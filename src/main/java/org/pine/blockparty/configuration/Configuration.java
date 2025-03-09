package org.pine.blockparty.configuration;

public enum Configuration {

    ARENA_FILE_PATH      ("arena-file-path", ""),
    STATISTICS_FILE_PATH ("stats-file-path", "plugins/blockparty/stats.json"),
    WORLD_NAME           ("world-name", "world");

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
