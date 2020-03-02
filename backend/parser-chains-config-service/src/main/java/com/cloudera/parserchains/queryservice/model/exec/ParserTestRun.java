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

import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ParserTestRun {

    public static class SampleData {
        private String type;
        private List<String> source;

        public SampleData() {
            this.source = new ArrayList<>();
        }

        public String getType() {
            return type;
        }

        public SampleData setType(String type) {
            this.type = type;
            return this;
        }

        public List<String> getSource() {
            return source;
        }

        public void setSource(List<String> source) {
            this.source = source;
        }

        public SampleData addSource(String toParse) {
            this.source.add(toParse);
            return this;
        }
    }

    public static class ResultLog {
        private String type;
        private String message;
        private String parserId;

        public String getType() {
            return type;
        }

        public ResultLog setType(String type) {
            this.type = type;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public ResultLog setMessage(String message) {
            this.message = message;
            return this;
        }

        public String getParserId() {
            return parserId;
        }

        public ResultLog setParserId(String parserId) {
            this.parserId = parserId;
            return this;
        }
    }

    @JsonProperty("sampleData")
    private SampleData sampleData;

    @JsonProperty("chainConfig")
    private ParserChainSchema parserChainSchema;

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
