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

package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.ParserBuilder;
import com.cloudera.parserchains.core.catalog.ParserCatalog;
import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.core.config.ConfigDescription;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.queryservice.model.summary.ObjectMapper;
import com.cloudera.parserchains.queryservice.model.describe.ParserDescriptor;
import com.cloudera.parserchains.queryservice.model.ParserID;
import com.cloudera.parserchains.queryservice.model.ParserName;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummary;
import com.cloudera.parserchains.queryservice.model.describe.ConfigDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DefaultParserDiscoveryService implements ParserDiscoveryService {
  static final String DEFAULT_SCHEMA_TYPE = "text";
  static final String DEFAULT_PATH_ROOT = "config";
  static final String PATH_DELIMITER = ".";

  @Autowired
  private ParserCatalog catalog;

  @Autowired
  private ParserBuilder builder;

  @Autowired
  private ObjectMapper<ParserSummary, ParserInfo> mapper;


  @Autowired
  public DefaultParserDiscoveryService(ParserCatalog catalog,
                                       ParserBuilder builder,
                                       ObjectMapper<ParserSummary, ParserInfo> mapper) {
    this.catalog = catalog;
    this.builder = builder;
    this.mapper = mapper;
  }

  @Override
  public List<ParserSummary> findAll() throws IOException {
    return catalog.getParsers()
            .stream()
            .map(info -> mapper.reform(info))
            .collect(Collectors.toList());
  }

  @Override
  public ParserDescriptor describe(ParserName name) throws IOException {
    return describeAll().get(name);
  }

  @Override
  public Map<ParserName, ParserDescriptor> describeAll() throws IOException {
    return catalog.getParsers()
            .stream()
            .collect(Collectors.toMap(
                    info -> ParserName.of(info.getName()),
                    info -> describeParser(info)));
  }

  private ParserDescriptor describeParser(ParserInfo parserInfo) {
    // describe the parser; the parserID == parser class
    ParserID id = ParserID.of(parserInfo.getParserClass().getCanonicalName());
    ParserName name = ParserName.of(parserInfo.getName());
    ParserDescriptor schema = new ParserDescriptor()
            .setParserID(id)
            .setParserName(name);

    Parser parser = builder.build(parserInfo);
    for(com.cloudera.parserchains.core.config.ConfigDescriptor param: parser.validConfigurations()) {

      // if >1 accepted values, use a custom path to 'group' the accepted values together, otherwise use root path
      String path = DEFAULT_PATH_ROOT;
      if(param.getAcceptedValues().size() > 1) {
        path = path + PATH_DELIMITER + param.getName().get();
      }

      for(Map.Entry<ConfigKey, ConfigDescription> entry: param.getAcceptedValues().entrySet()) {
        ConfigKey configKey = entry.getKey();
        ConfigDescription description = entry.getValue();
        ConfigDescriptor item = new ConfigDescriptor()
                .setName(configKey.getKey())
                .setLabel(param.getDescription().get())
                .setDescription(description.get())
                .setPath(path)
                .setRequired(Boolean.toString(param.isRequired()))
                .setType(DEFAULT_SCHEMA_TYPE);
        schema.addSchemaItem(item);
      }
    }

    return schema;
  }
}