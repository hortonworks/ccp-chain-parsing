package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValues;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.RouterLink;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    private Throwable error;
    private Configurer configurer;

    public AlwaysFailParser() {
        error = new IllegalStateException("Parsing error encountered");
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
        Objects.requireNonNull(message);
        error = new IllegalStateException(message);
        return this;
    }

    public AlwaysFailParser withError(Throwable error) {
        this.error = Objects.requireNonNull(error);
        return this;
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, ConfigValues values) {
       configurer.configure(name, values);
    }

    Throwable getError() {
        return error;
    }

    /**
     * Handles configuration for the {@link AlwaysFailParser}.
     */
    static class Configurer {
        static final ConfigDescriptor errorMessageConfig = ConfigDescriptor
                .builder()
                .name("Error Message")
                .description("The error message to throw.")
                .isRequired(false)
                .build();
        private AlwaysFailParser parser;

        public Configurer(AlwaysFailParser parser) {
            this.parser = parser;
        }

        public List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(errorMessageConfig);
        }

        public void configure(ConfigName name, ConfigValues values) {
            if(errorMessageConfig.getName().equals(name)) {
                values.getValue().ifPresent(value -> {
                    parser.withError(value.getValue());
                });

            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
            }
        }
    }
}
