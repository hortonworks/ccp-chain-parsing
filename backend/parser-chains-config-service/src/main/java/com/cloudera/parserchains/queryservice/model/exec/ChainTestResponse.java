package com.cloudera.parserchains.queryservice.model.exec;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the data model for the response to a parser chain
 * test.
 *
 * <p>Describes the result of testing a parser chain by parsing a
 * message.
 *
 * <p>This is the top-level class for the response message for the
 * "Live View" feature.
 *
 * <p>See also {@link ChainTestRequest}.
 */
public class ChainTestResponse {

    /**
     * The individual parser results; one for each sample/message being tested.
     */
    List<ParserResult> results;

    public ChainTestResponse() {
        this(new ArrayList<>());
    }

    public ChainTestResponse(List<ParserResult> results) {
        this.results = results;
    }

    public ChainTestResponse(ParserResult result) {
        this.results = new ArrayList<>();
        this.results.add(result);
    }

    public List<ParserResult> getResults() {
        return results;
    }

    public ChainTestResponse setResults(List<ParserResult> results) {
        this.results = results;
        return this;
    }

    public ChainTestResponse addResult(ParserResult result) {
        this.results.add(result);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChainTestResponse that = (ChainTestResponse) o;
        return new EqualsBuilder()
                .append(results, that.results)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(results)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ParserResults{" +
                "results=" + results +
                '}';
    }
}
