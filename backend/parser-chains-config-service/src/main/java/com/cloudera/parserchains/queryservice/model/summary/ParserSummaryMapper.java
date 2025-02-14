package com.cloudera.parserchains.queryservice.model.summary;

import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.core.model.define.ParserID;

public class ParserSummaryMapper implements ObjectMapper<ParserSummary, ParserInfo> {

    @Override
    public ParserSummary reform(ParserInfo source) {
        return new ParserSummary()
                .setId(ParserID.of(source.getParserClass()))
                .setName(source.getName())
                .setDescription(source.getDescription());
    }

    @Override
    public ParserInfo transform(ParserSummary source) {
        String clazzName = source.getId().getId();
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch(ClassNotFoundException e) {
            String msg = String.format("Parser class not found; class=%s", clazzName);
            throw new IllegalArgumentException(msg, e);
        }

        if(Parser.class.isAssignableFrom(clazz)) {
            // the cast is guaranteed to be safe because of the 'if' condition above
            @SuppressWarnings("unchecked")
            Class<com.cloudera.parserchains.core.Parser> parserClass = (Class<Parser>) clazz;
            return ParserInfo.builder()
                    .parserClass(parserClass)
                    .name(source.getName().getName())
                    .description(source.getDescription())
                    .build();

        } else {
            String msg = String.format("Parser class is not a valid parser; class=%s", clazzName);
            throw new IllegalArgumentException(msg);
        }
    }
}
