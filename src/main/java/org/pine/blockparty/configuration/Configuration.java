package org.pine.blockparty.configuration;

public enum Configuration {

    ARENA_FILE_PATH      ("arena-file-path", "plugins/blockparty/levels.json"),
    STATISTICS_FILE_PATH ("stats-file-path", "plugins/blockparty/stats.json");

    private final String key;
    private final Object defaultValue;

    Configuration(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
