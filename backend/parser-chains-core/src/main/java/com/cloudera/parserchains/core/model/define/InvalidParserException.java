package com.cloudera.parserchains.core.model.define;

/**
 * An exception that occurs when the user has defined a parser that is invalid
 * and is impossible to construct.
 */
public class InvalidParserException extends Exception {
    private ParserSchema badParser;

    /**
     * @param badParser The parser that caused this error.
     * @param cause The root cause exception.
     */
    public InvalidParserException(ParserSchema badParser, Exception cause) {
        super(cause);
        this.badParser = badParser;
    }

    /**
     * @param badParser The parser that caused this error.
     * @param message The error message.
     */
    public InvalidParserException(ParserSchema badParser, String message) {
        super(message);
        this.badParser = badParser;
    }

    public ParserSchema getBadParser() {
        return badParser;
    }
}
