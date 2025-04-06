package org.pine.blockparty.managers;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.pine.blockparty.configuration.Configuration;
import org.pine.blockparty.exceptions.ConfigurationMissingException;

public class ConfigurationManager {

    private final FileConfiguration fileConfiguration;

    public ConfigurationManager(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public String getConfigurationValue(Configuration configuration) {
        if (configuration == null || StringUtils.isEmpty(configuration.getKey())) {
            throw new ConfigurationMissingException("Configuration enum is null");
        }

        final String value = fileConfiguration.getString(configuration.getKey());

        if (!StringUtils.isEmpty(value)) {
            return value;
        } else if (StringUtils.isEmpty(configuration.getDefaultValue())) {
            throw new ConfigurationMissingException("Configuration missing and no default value for configuration: " + configuration);
        }

        return configuration.getDefaultValue();
    }
}
