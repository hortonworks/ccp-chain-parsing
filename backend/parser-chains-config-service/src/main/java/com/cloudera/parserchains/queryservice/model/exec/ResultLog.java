package com.cloudera.parserchains.queryservice.model.exec;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Describes the result of parsing a single message.
 *
 * <p>See also {@link ParserTestRun} which is the top-level class for the
 * data model used for the "Live View" feature.
 */
public class ResultLog {
    public static final String DEFAULT_SUCCESS_MESSAGE = "success";
    public static final String INFO_TYPE = "info";
    public static final String ERROR_TYPE = "error";

    private String type;
    private String message;
    private String parserId;

    /**
     * Create a result that is returned when a message is successfully
     * parsed by a parser chain.
     * @param lastParserId The id of the last parser in the parser chain.
     * @param message A message describing what happened that will be displayed to the user.
     * @return A {@link ResultLog}
     */
    public static ResultLog success(String lastParserId, String message) {
        ResultLog log = new ResultLog();
        log.type = INFO_TYPE;
        log.message = message;
        log.parserId = lastParserId;
        return log;
    }

    /**
     * Create a result that is returned when a message is successfully
     * parsed by a parser chain.
     * @param lastParserId The id of the last parser in the parser chain.
     * @return A {@link ResultLog}
     */
    public static ResultLog success(String lastParserId) {
        return success(lastParserId, DEFAULT_SUCCESS_MESSAGE);
    }

    /**
     * Creates a result that is returned when an error occurs while
     * parsing a message with a parser chain.
     * @param parserId The id of the parser that failed.
     * @param message A message describing the error condition that will be displayed to the user.
     * @return A {@link ResultLog}
     */
    public static ResultLog error(String parserId, String message) {
        ResultLog log = new ResultLog();
        log.type = ERROR_TYPE;
        log.message = message;
        log.parserId = parserId;
        return log;
    }

    /**
     * Use the static factory methods instead. See {@link #success(String)}, {@link #success(String, String)},
     * or {@link #error(String, String)}.
     */
    private ResultLog() {
        // do not use
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getParserId() {
        return parserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResultLog resultLog = (ResultLog) o;
        return new EqualsBuilder()
                .append(type, resultLog.type)
                .append(message, resultLog.message)
                .append(parserId, resultLog.parserId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(message)
                .append(parserId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ResultLog{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", parserId='" + parserId + '\'' +
                '}';
    }
}
