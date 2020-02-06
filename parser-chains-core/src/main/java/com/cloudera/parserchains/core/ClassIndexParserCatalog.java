package com.cloudera.parserchains.core;

import org.atteo.classindex.ClassIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ParserCatalog} that builds a catalog of parsers using a class index
 * compiled at build time.
 *
 * https://github.com/atteo/classindex
 */
public class ClassIndexParserCatalog implements ParserCatalog {
    Logger logger = LoggerFactory.getLogger(ChainRunner.class);

    @Override
    public List<ParserInfo> getParsers() {
        List<ParserInfo> results = new ArrayList<>();

        // search the class index for the annotation
        Iterable<Class<?>> knownAnnotations = ClassIndex.getAnnotated(MessageParser.class);
        for(Class<?> clazz: knownAnnotations) {
            MessageParser annotation = clazz.getAnnotation(MessageParser.class);

            // parsers must implement the parser interface
            if(Parser.class.isAssignableFrom(clazz)) {
                // found a parser
                Class<Parser> parserClass = (Class<Parser>) clazz;
                ParserInfo parserInfo = ParserInfo.builder()
                        .withName(annotation.name())
                        .withDescription(annotation.description())
                        .withParserClass(parserClass)
                        .build();
                results.add(parserInfo);
            } else {
                logger.warn("Found class with annotation '{}' that does not implement '{}'; class={}",
                        MessageParser.class.getName(), Parser.class.getName(), clazz.getName());
            }
        }

        return results;
    }
}
