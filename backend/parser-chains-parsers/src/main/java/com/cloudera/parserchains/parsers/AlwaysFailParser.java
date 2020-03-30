package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.RouterLink;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.model.config.ConfigDescriptor;
import com.cloudera.parserchains.core.model.config.ConfigKey;
import com.cloudera.parserchains.core.model.config.ConfigName;
import com.cloudera.parserchains.core.model.config.ConfigValue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Parser} that always fails.
 *
 * <p>This can be used with a {@link RouterLink}
 * to flag when unexpected conditions are encountered in the data.
 */
@MessageParser(
    name="Error",
    description = "Always results in an error. Can be used with a router to flag unexpected data.")
public class AlwaysFailParser implements Parser {
    private static final String DEFAULT_ERROR_MESSAGE = "Parsing error encountered";
    private Throwable error;
    private Configurer configurer;

    public AlwaysFailParser() {
        error = new IllegalStateException(DEFAULT_ERROR_MESSAGE);
        configurer = new Configurer(this);
    }

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .withError(error)
                .build();
    }

    public AlwaysFailParser withError(String message) {
        Objects.requireNonNull(message, "A valid error message is required.");
        error = new IllegalStateException(message);
        return this;
    }

    public AlwaysFailParser withError(Throwable error) {
        this.error = Objects.requireNonNull(error, "A valid error message is required.");
        return this;
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
       configurer.configure(name, values);
    }

    Throwable getError() {
        return error;
    }

    /**
     * Handles configuration for the {@link AlwaysFailParser}.
     */
    static class Configurer {
        static final ConfigKey errorMessageKey = ConfigKey.builder()
                .key("errorMessage")
                .label("Message")
                .description("The error message.")
                .defaultValue(DEFAULT_ERROR_MESSAGE)
                .build();
        static final ConfigDescriptor errorMessageConfig = ConfigDescriptor
                .builder()
                .acceptsValue(errorMessageKey)
                .isRequired(false)
                .build();
        private AlwaysFailParser parser;

        public Configurer(AlwaysFailParser parser) {
            this.parser = parser;
        }

        public List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(errorMessageConfig);
        }

        public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            if(errorMessageConfig.getName().equals(name)) {
                configureErrorMessage(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name.get()));
            }
        }

        private void configureErrorMessage(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(errorMessageKey)).ifPresent(
                    value -> parser.withError(value.get())
            );
        }
    }
}
