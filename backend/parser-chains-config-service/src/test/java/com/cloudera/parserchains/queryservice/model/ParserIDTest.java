package com.cloudera.parserchains.queryservice.model;

import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;

public class ParserIDTest {

    @Test
    void fromClassName() {
        ParserID id = ParserID.of(ParserIDTest.class);
        assertEquals("com.cloudera.parserchains.queryservice.model.ParserIDTest", id.getId());
    }

    @Test
    void fromString() {
        final String expected = "com.foo.bar.Parser";
        ParserID id = ParserID.of(expected);
        assertEquals(expected, id.getId());
    }

    /**
     * "com.foo.bar.Parser"
     */
    @Multiline
    private String expectedJSON;

    @Test
    void toJSON() throws JsonProcessingException {
        ParserID id = ParserID.of("com.foo.bar.Parser");
        String actual = JSONUtils.INSTANCE.toJSON(id, false);
        assertThat(actual, equalToCompressingWhiteSpace(expectedJSON));
    }
}
