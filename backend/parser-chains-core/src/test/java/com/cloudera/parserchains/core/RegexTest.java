package com.cloudera.parserchains.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.Test;

public class RegexTest {
    
    @Test
    void matchString() {
        Regex regex = Regex.of("^[A-Z]{1,2}$");
        assertFalse(regex.matches(""));
        assertTrue(regex.matches("B"));
        assertTrue(regex.matches("BB"));
        assertFalse(regex.matches("BBB"));
    }

    @Test
    void matchFieldName() {
        Regex regex = Regex.of("^[A-Z]{0,2}$");
        assertTrue(regex.matches(FieldName.of("B")));
        assertTrue(regex.matches(FieldName.of("BB")));
        assertFalse(regex.matches(FieldName.of("BBB")));
    }

    @Test
    void matchFieldValue() {
        Regex regex = Regex.of("^[A-Z]{0,2}$");
        assertTrue(regex.matches(FieldValue.of("B")));
        assertTrue(regex.matches(FieldValue.of("BB")));
        assertFalse(regex.matches(FieldValue.of("BBB")));
    }
    
    @Test
    void invalidRegex() {
        assertThrows(PatternSyntaxException.class, () -> Regex.of("[[["));
    }
    
    @Test
    void noNulls() {
        assertThrows(NullPointerException.class, () -> Regex.of(null));
    }
}
