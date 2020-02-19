package com.cloudera.parserchains.core.catalog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atteo.classindex.ClassIndex;

import java.util.ArrayList;
import java.util.List;

import com.cloudera.parserchains.core.Parser;

/**
 * A {@link ParserCatalog} that builds a catalog of parsers using a class index
 * compiled at build time.
 *
 * <p>A parser must be marked using the {@link MessageParser} annotation
 * so that the parser is discoverable using this class.
 *
 * https://github.com/atteo/classindex
 */
public class ClassIndexParserCatalog implements ParserCatalog {
    private static final Logger logger = LogManager.getLogger(ClassIndexParserCatalog.class);

    @Override
    public List<ParserInfo> getParsers() {
        List<ParserInfo> results = new ArrayList<>();

        // search the class index for the annotation
        Iterable<Class<?>> knownAnnotations = ClassIndex.getAnnotated(MessageParser.class);
        for(Class<?> clazz: knownAnnotations) {
            MessageParser annotation = clazz.getAnnotation(MessageParser.class);

            // parsers must implement the parser interface
            if(Parser.class.isAssignableFrom(clazz)) {
                // found a parser.  the cast is guaranteed to be safe because of the 'if' condition above
                @SuppressWarnings("unchecked")
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

        if(logger.isDebugEnabled()) {
            for(ParserInfo parserInfo: results) {
                logger.debug("Found parser: class={}, name={}, desc={}",
                        parserInfo.getParserClass(),
                        parserInfo.getName(),
                        parserInfo.getDescription());
            }
        }
        return results;
    }
}
