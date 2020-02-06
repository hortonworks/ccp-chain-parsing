package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigName;
import com.cloudera.parserchains.core.ConfigValue;

import java.util.List;

public final class ParserUtils {

    private ParserUtils() {
        // do not instantiate this class
    }

    /**
     * Expect the config values to be of size N.
     * @param name The name of the config value.
     * @param values The config values.
     * @param expectedSize The expected number of config values.
     */
    public static void requireN(ConfigName name, List<ConfigValue> values, int expectedSize) {
        if(values.size() != expectedSize) {
            String msg = "For '%s' expected %d value(s), but got %d; ";
            throw new IllegalArgumentException(String.format(msg, name.getName(), expectedSize, values.size()));
        }
    }
}
