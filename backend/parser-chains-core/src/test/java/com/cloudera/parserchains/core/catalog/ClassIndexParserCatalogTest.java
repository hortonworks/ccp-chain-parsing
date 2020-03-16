package com.cloudera.parserchains.core.catalog;

import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.model.config.ConfigDescriptor;
import com.cloudera.parserchains.core.model.config.ConfigKey;
import com.cloudera.parserchains.core.model.config.ConfigName;
import com.cloudera.parserchains.core.model.config.ConfigValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ClassIndexParserCatalogTest {

    @MessageParser(name="Fake Parser", description="Parser created for catalog tests.")
    private static class FakeParser implements Parser {
        @Override
        public Message parse(Message message) {
            // do nothing
            return null;
        }

        @Override
        public List<ConfigDescriptor> validConfigurations() {
            // do nothing
            return null;
        }

        @Override
        public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            // do nothing
        }
    }

    @Test
    void findParser() {
        ParserCatalog catalog = new ClassIndexParserCatalog();
        List<ParserInfo> parsers = catalog.getParsers();
        boolean foundFakeParser = false;
        for(ParserInfo parserInfo: parsers) {
            if(FakeParser.class.equals(parserInfo.getParserClass())) {
                // found the fake parser
                foundFakeParser = true;
                assertEquals("Fake Parser", parserInfo.getName());
                assertEquals("Parser created for catalog tests.", parserInfo.getDescription());
            }
        }
        assertTrue(foundFakeParser);
    }

    /**
     * A parser that does not implement {@link Parser}.  This is not a valid parser.
     */
    @MessageParser(name="Bad Parser", description="A description.")
    private static class BadParser { }

    @Test
    void badParser() {
        ParserCatalog catalog = new ClassIndexParserCatalog();
        List<ParserInfo> parsers = catalog.getParsers();
        for(ParserInfo parserInfo: parsers) {
            if(BadParser.class.equals(parserInfo.getParserClass())) {
                fail("Should not have 'found' this bad parser.");
            }
        }
    }
}
