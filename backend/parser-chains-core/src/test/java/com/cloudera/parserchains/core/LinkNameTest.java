package com.cloudera.parserchains.core;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LinkNameTest {
    private static final String name = "parser1";

    @Test
    void valid() {
        LinkName.of("Link names can contain letters and whitespace");
        LinkName.of("Link names can contain numbers 0123456789");
        LinkName.of("Link names can contain some punctuation  , - . : @");
        String maxLengthName = StringUtils.repeat("A", 120);
        LinkName.of(maxLengthName);
    }

    @Test
    void uuidIsValid() {
        LinkName.of(UUID.randomUUID().toString());
        LinkName.of(UUID.randomUUID().toString());
        LinkName.of(UUID.randomUUID().toString());
        LinkName.of(UUID.randomUUID().toString());
    }

    @Test
    void tooLong() {
        String tooLong = StringUtils.repeat("A", 121);
        assertThrows(IllegalArgumentException.class, () -> LinkName.of(tooLong));
    }

    @Test
    void tooShort() {
        assertThrows(IllegalArgumentException.class, () -> LinkName.of(""));
    }

    @Test
    void notNull() {
        assertThrows(IllegalArgumentException.class, () -> LinkName.of(null));
    }

    @Test
    void invalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> LinkName.of("<html></html>"));
    }

    @Test
    void get() {
        assertEquals(name, LinkName.of(name).get());
    }
}
