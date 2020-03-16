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

import com.cloudera.parserchains.core.model.define.ParserChainSchema;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Defines the data model for when a request is made to execute a parser chain on
 * a set of sample data.  This is also called the "Live View" feature.
 *
 * <p>This is the top-level class defining the data model for the "Live View" feature.
 */
public class ParserTestRun {

    /**
     * The sample data that needs to be parsed.
     */
    @JsonProperty("sampleData")
    private SampleData sampleData;

    /**
     * Defines the parser chain that needs to be constructed.
     */
    @JsonProperty("chainConfig")
    private ParserChainSchema parserChainSchema;

    /**
     * Describes the result of parsing each message with the parser chain.
     * <p>There should be one result for each message received.
     */
    @JsonProperty("result")
    private List<ParserResult> result;

    public SampleData getSampleData() {
        return sampleData;
    }

    public void setSampleData(SampleData sampleData) {
        this.sampleData = sampleData;
    }

    public ParserChainSchema getParserChainSchema() {
        return parserChainSchema;
    }

    public void setParserChainSchema(ParserChainSchema parserChainSchema) {
        this.parserChainSchema = parserChainSchema;
    }

    public List<ParserResult> getResult() {
        return result;
    }

    public void setResult(List<ParserResult> result) {
        this.result = result;
    }
}
