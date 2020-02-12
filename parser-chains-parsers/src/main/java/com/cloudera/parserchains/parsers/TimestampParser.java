package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A parser that adds the current system time as a field to the message. Useful for
 * tracking the time when a message was parsed.
 */
public class TimestampParser implements Parser {

    public static class Clock {
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    private FieldName outputField;
    private Clock clock;

    public TimestampParser() {
        this.outputField = FieldName.of("timestamp");
        this.clock = new Clock();
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
}
