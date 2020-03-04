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

import java.util.HashMap;
import java.util.Map;

/**
 * The result of parsing a message with a parser chain.
 *
 * <p>A result is captured for each parser in a parser chain. This
 * describes the intermediate steps involved in parsing a message with
 * a parser chain.
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
    private ParserTestRun.ResultLog log;

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

    public Map<String, String> getOutput() {
        return output;
    }

    public ParserResult setOutput(Map<String, String> output) {
        this.output = output;
        return this;
    }

    public ParserTestRun.ResultLog getLog() {
        return log;
    }

    public ParserResult setLog(ParserTestRun.ResultLog log) {
        this.log = log;
        return this;
    }
}
