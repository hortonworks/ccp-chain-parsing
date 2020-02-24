package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValues;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A parser that adds the current system time as a field to the message. Useful for
 * tracking the time when a message was parsed.
 */
@MessageParser(
    name="Timestamp",
    description="Adds a timestamp to a message. Can be used to mark processing time.")
public class TimestampParser implements Parser {
    private FieldName outputField;
    private Clock clock;
    private Configurer configurer;

    public TimestampParser() {
        this.outputField = FieldName.of("timestamp");
        this.clock = new Clock();
        this.configurer = new Configurer(this);
    }

    @Override
    public Message parse(Message input) {
        Long now = clock.currentTimeMillis();
        FieldValue timestamp = FieldValue.of(Long.toString(now));
        return Message.builder()
                .withFields(input)
                .addField(outputField, timestamp)
                .build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Arrays.asList(outputField);
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, ConfigValues values) {
        configurer.configure(name, values);
    }

    /**
     * @param fieldName The name of the field added to each message.
     */
    public TimestampParser withOutputField(FieldName fieldName) {
        this.outputField = Objects.requireNonNull(fieldName);
        return this;
    }

    /**
     * @param clock A {@link Clock} to use during testing.
     */
    public TimestampParser withClock(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
        return this;
    }

    public FieldName getOutputField() {
        return outputField;
    }

    /**
     * The source of the current timestamp. Enables testing.
     */
    public static class Clock {
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    /**
     * Handles configuration for the {@link TimestampParser}.
     */
    static class Configurer {
        static final ConfigDescriptor outputFieldConfig = ConfigDescriptor.builder()
                .name("Output Field")
                .description("The name of the field that will contain the timestamp.  Defaults to 'timestamp'.")
                .isRequired(false)
                .build();
        private TimestampParser parser;

        public Configurer(TimestampParser parser) {
            this.parser = parser;
        }

        public List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(outputFieldConfig);
        }

        public void configure(ConfigName name, ConfigValues values) {
            if(outputFieldConfig.getName().equals(name)) {
                configureOutputField(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
            }
        }

        private void configureOutputField(ConfigValues values) {
            values.getValue().ifPresent(value -> {
                FieldName outputField = FieldName.of(value.getValue());
                parser.withOutputField(outputField);
            });
        }
    }
}
