package com.cloudera.parserchains.queryservice.model.exec;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes the result of parsing a message with a parser chain.
 *
 * <p>See also {@link ParserTestRun} which is the top-level class for the
 * data model used for the "Live View" feature.
 */
public class ParserResults {

    /**
     * The individual parser results; one for each parser in the chain.
     */
    List<ParserResult> results;

    public ParserResults() {
        this(new ArrayList<>());
    }

    public ParserResults(List<ParserResult> results) {
        this.results = results;
    }

    public ParserResults(ParserResult result) {
        this.results = new ArrayList<>();
        this.results.add(result);
    }

    public List<ParserResult> getResults() {
        return results;
    }

    public void setResults(List<ParserResult> results) {
        this.results = results;
    }

    public ParserResults addResult(ParserResult result) {
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
        ParserResults that = (ParserResults) o;
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
