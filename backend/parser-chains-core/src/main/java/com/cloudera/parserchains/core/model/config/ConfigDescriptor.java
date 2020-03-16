package com.cloudera.parserchains.core.model.config;

import com.cloudera.parserchains.core.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Fully describes a parameter that is used to configure a {@link Parser}.
 *
 * <p>A parser author exposes the configuration parameters available for
 * their parser using this class along with the {@link Parser#validConfigurations()}
 * method.
 */
public class ConfigDescriptor {
    private final ConfigName name;
    private final ConfigDescription description;
    private final boolean isRequired;
    private final List<ConfigKey> acceptedValues;

    /**
     * A builder that can be used to construct Creates a {@link ConfigDescriptor}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Private constructor. See ConfigDescriptor{@link #builder()}.
     */
    private ConfigDescriptor(ConfigName name,
                             ConfigDescription description,
                             boolean isRequired,
                             List<ConfigKey> acceptedValues) {
        this.name = Objects.requireNonNull(name, "Name is required.");
        this.description = Objects.requireNonNull(description, "Description is required.");
        this.isRequired = isRequired;
        this.acceptedValues = Objects.requireNonNull(acceptedValues, "Values are required");
    }

    /**
     * @return The name of the configuration parameter.
     */
    public ConfigName getName() {
        return name;
    }

    /**
     * @return A description of the configuration parameter.
     */
    public ConfigDescription getDescription() {
        return description;
    }

    /**
     * @return If the configuration parameter is required or not.
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * @return The values required by this configuration parameter.
     */
    public List<ConfigKey> getAcceptedValues() {
        return Collections.unmodifiableList(acceptedValues);
    }

    public static class Builder {
        private ConfigName name;
        private ConfigDescription description;
        private boolean isRequired;
        private List<ConfigKey> acceptedValues;

        public Builder() {
            this.acceptedValues = new ArrayList<>();
        }

        public Builder name(ConfigName name) {
            this.name = Objects.requireNonNull(name, "A name is required.");
            return this;
        }

        public Builder name(String name) {
            return name(ConfigName.of(name));
        }

        public Builder description(ConfigDescription description) {
            this.description = Objects.requireNonNull(description, "A description is required.");
            return this;
        }

        public Builder description(String description) {
            return description(ConfigDescription.of(description));
        }

        public Builder isRequired(boolean isRequired) {
            this.isRequired = isRequired;
            return this;
        }

        public Builder acceptsValue(ConfigKey key) {
            this.acceptedValues.add(key);
            return this;
        }

        public Builder acceptsValue(String key, String label, String description) {
            ConfigKey configKey = ConfigKey.builder()
                    .key(key)
                    .label(label)
                    .description(description)
                    .build();
            return acceptsValue(configKey);
        }

        public ConfigDescriptor build() {
            if(acceptedValues.size() == 0) {
                throw new IllegalArgumentException("Must define at least 1 required value.");
            }

            // shortcut - if name not defined, use the name associated with the ConfigKey
            if(name == null && acceptedValues.size() > 0) {
                name = ConfigName.of(acceptedValues.get(0).getKey());
            }

            // shortcut - if description not defined, use the name associated with the ConfigKey
            if(description == null && acceptedValues.size() > 0) {
                description = acceptedValues.get(0).getDescription();
            }

            return new ConfigDescriptor(name, description, isRequired, acceptedValues);
        }
    }
}
