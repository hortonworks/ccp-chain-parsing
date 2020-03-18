package com.cloudera.parserchains.core;

import com.cloudera.parserchains.core.catalog.ParserInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A {@link ParserBuilder} that uses Java's Reflection API to build a {@link Parser}.
 */
public class ReflectiveParserBuilder implements ParserBuilder {

    @Override
    public Parser build(ParserInfo parserInfo) {
        Parser parser;
        try {
            Constructor<? extends Parser> constructor = parserInfo.getParserClass().getConstructor();
            parser = constructor.newInstance();

        } catch(NoSuchMethodException e) {
            throw new RuntimeException("A default constructor is required; class=" +
                    parserInfo.getParserClass().getCanonicalName(), e);

        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to instantiate the parser; class=" +
                    parserInfo.getParserClass().getCanonicalName(), e);
        }
        return parser;
    }
}
