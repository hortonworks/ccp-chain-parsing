package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
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
 * A parser that adds the current system time as a field to the message. Useful for
 * tracking the time when a message was parsed.
 */
@MessageParser(
    name="Timestamp",
    description="Adds a timestamp to a message. Can be used to mark processing time.")
public class TimestampParser implements Parser {
    private static final FieldName DEFAULT_OUTPUT_FIELD = FieldName.of("timestamp");
    private FieldName outputField;
    private Clock clock;
    private Configurer configurer;

    public TimestampParser() {
        this.outputField = DEFAULT_OUTPUT_FIELD;
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
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
        configurer.configure(name, values);
    }

    /**
     * @param fieldName The name of the field added to each message.
     */
    public TimestampParser withOutputField(FieldName fieldName) {
        this.outputField = Objects.requireNonNull(fieldName, "A valid output field is required.");
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
        static final ConfigKey outputFieldKey = ConfigKey.builder()
                .key("outputField")
                .label("Output Field")
                .description("The name of the field that will contain the timestamp.  Defaults to 'timestamp'.")
                .defaultValue(DEFAULT_OUTPUT_FIELD.get())
                .build();
        static final ConfigDescriptor outputFieldConfig = ConfigDescriptor.builder()
                .name("outputField")
                .description("Output Field")
                .acceptsValue(outputFieldKey)
                .isRequired(false)
                .build();
        private TimestampParser parser;

        public Configurer(TimestampParser parser) {
            this.parser = parser;
        }

        public List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(outputFieldConfig);
        }

        public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            if(outputFieldConfig.getName().equals(name)) {
                configureOutputField(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name.get()));
            }
        }

        private void configureOutputField(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(outputFieldKey)).ifPresent(value -> {
                FieldName outputField = FieldName.of(value.get());
                parser.withOutputField(outputField);
            });
        }
    }
}
