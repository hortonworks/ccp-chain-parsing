/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.parserchains.queryservice.model.exec;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * The result of parsing a message with a parser chain.
 *
 * <p>A result is captured for each parser in a parser chain. This
 * describes the intermediate steps involved in parsing a message with
 * a parser chain.
 *
 * <p>See also {@link ChainTestRequest} which is the top-level class for the
 * data model used for the "Live View" feature.
 */
public class ParserResult {
    /**
     * The input fields provided to the parser.
     */
    private Map<String, String> input;

    /**
     * The output fields produced by the parser.
     */
    private Map<String, String> output;

    /**
     * Describes the outcome of parsing the message.
     */
    private ResultLog log;

    public ParserResult() {
        this.input = new HashMap<>();
        this.output = new HashMap<>();
    }

    public Map<String, String> getInput() {
        return input;
    }

    public ParserResult setInput(Map<String, String> input) {
        this.input = input;
        return this;
    }

    public ParserResult addInput(String fieldName, String fieldValue) {
        this.input.put(fieldName, fieldValue);
        return this;
    }

    public Map<String, String> getOutput() {
        return output;
    }

    public ParserResult setOutput(Map<String, String> output) {
        this.output = output;
        return this;
    }

    public ParserResult addOutput(String fieldName, String fieldValue) {
        this.output.put(fieldName, fieldValue);
        return this;
    }

    public ResultLog getLog() {
        return log;
    }

    public ParserResult setLog(ResultLog log) {
        this.log = log;
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
        ParserResult that = (ParserResult) o;
        return new EqualsBuilder()
                .append(input, that.input)
                .append(output, that.output)
                .append(log, that.log)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(input)
                .append(output)
                .append(log)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ParserResult{" +
                "input=" + input +
                ", output=" + output +
                ", log=" + log +
                '}';
    }
}
