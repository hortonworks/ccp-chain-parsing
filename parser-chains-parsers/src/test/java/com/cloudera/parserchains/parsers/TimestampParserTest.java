package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigValue;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TimestampParserTest {

    public static class FixedClock extends TimestampParser.Clock {
        private long currentTimeMillis;

        public FixedClock(long currentTimeMillis) {
            this.currentTimeMillis = currentTimeMillis;
        }

        @Override
        public long currentTimeMillis() {
            return currentTimeMillis;
        }
    }

    @Test
    void addTimestamp() {
        long time = 1426349294842L;
        Message input = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .build();

        FieldName timestampField = FieldName.of("processing_timestamp");
        Message output = new TimestampParser()
                .withClock(new FixedClock(time))
                .withOutputField(timestampField)
                .parse(input);

        // expect a new timestamp field to have been added
        assertEquals(FieldValue.of(Long.toString(time)), output.getField(timestampField).get());

        // expect the same input fields on the output side
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("field1")).get());

        // no errors
        assertFalse(output.getError().isPresent());
    }

    @Test
    void defaultTimestampField() {
        long time = 1426349294842L;
        Message input = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .build();

        TimestampParser parser = new TimestampParser();
        Message output = parser
                .withClock(new FixedClock(time))
                .parse(input);

        // expect a new timestamp field to have been added using the default name
        FieldName defaultFieldName = parser.getOutputField();
        assertEquals(FieldValue.of(Long.toString(time)), output.getField(defaultFieldName).get());

        // expect the same input fields on the output side
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("field1")).get());

        // no errors
        assertFalse(output.getError().isPresent());
    }

    @Test
    void configureTimestampField() {
        TimestampParser parser = new TimestampParser();
        parser.configure(TimestampParser.outputFieldConfig, Arrays.asList(ConfigValue.of("processing_time")));
        assertEquals(FieldName.of("processing_time"), parser.getOutputField());
    }
}
