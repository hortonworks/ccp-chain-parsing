package com.cloudera.parserchains.core;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FieldNameTest {
    static String name = "syslog.structuredData.exampleSDID@32480.iut";

    @Test
    void valid() {
        FieldName.of(name);
        FieldName.of("Field names can contain letters and whitespace");
        FieldName.of("Field names can contain numbers 0123456789");
        FieldName.of("Field names can contain some punctuation  , - . : @");
        String maxLengthName = StringUtils.repeat("A", 120);
        FieldName.of(maxLengthName);
    }

    @Test
    void tooLong() {
        String tooLong = StringUtils.repeat("A", 121);
        assertThrows(IllegalArgumentException.class, () -> FieldName.of(tooLong));
    }

    @Test
    void notNull() {
        assertThrows(IllegalArgumentException.class, () -> FieldName.of(null));
    }

    @Test
    void get() {
        assertEquals(name, FieldName.of(name).get());
    }
}
