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
import com.cloudera.parserchains.core.model.config.ConfigDescriptor;
import com.cloudera.parserchains.core.model.config.ConfigKey;
import com.cloudera.parserchains.core.model.define.ParserID;
import com.cloudera.parserchains.core.model.define.ParserName;
import com.cloudera.parserchains.queryservice.model.describe.ConfigParamDescriptor;
import com.cloudera.parserchains.queryservice.model.describe.ParserDescriptor;
import com.cloudera.parserchains.queryservice.model.summary.ObjectMapper;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummary;
import org.apache.commons.collections4.ListUtils;
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
  public ParserDescriptor describe(ParserID name) throws IOException {
    return describeAll().get(name);
  }

  @Override
  public Map<ParserID, ParserDescriptor> describeAll() throws IOException {
    return catalog.getParsers()
            .stream()
            .collect(Collectors.toMap(
                    info -> ParserID.of(info.getParserClass()),
                    info -> describeParser(info)));
  }

  private ParserDescriptor describeParser(ParserInfo parserInfo) {
    // describe the parser; the parserID == parser class
    ParserID id = ParserID.of(parserInfo.getParserClass());
    ParserName name = ParserName.of(parserInfo.getName());
    ParserDescriptor descriptor = new ParserDescriptor()
            .setParserID(id)
            .setParserName(name);

    Parser parser = builder.build(parserInfo);
    for(ConfigDescriptor param: ListUtils.emptyIfNull(parser.validConfigurations())) {

      // describe each parameter accepted by the parser
      for(ConfigKey configKey: param.getAcceptedValues()) {
        /*
         * If multiple=true, the front-end expects values to be contained within an array. if
         * multiple=false, the value should NOT be wrapped in an array; just a single map.
         * Currently, the backend always wraps values, even single values, in arrays.
         *
         * Having the backend adhere to what the front-end expects will take some additional
         * work. As a work-around all configurations are marked as accepting multiple values,
         * even those that do not.  The consequence of this is that all fields will show the blue,
         * plus icon to add a field.
         */
        final boolean multiple = true;
        ConfigParamDescriptor paramDescriptor = new ConfigParamDescriptor()
                .setName(configKey.getKey())
                .setLabel(configKey.getLabel())
                .setDescription(configKey.getDescription().get())
                .setPath(DEFAULT_PATH_ROOT + PATH_DELIMITER + param.getName().get())
                .setRequired(param.isRequired())
                .setType(DEFAULT_SCHEMA_TYPE)
                .setMultiple(multiple);

        // set a default value, if one exists for this parameter
        configKey.getDefaultValue().ifPresent(defaultVal ->
                paramDescriptor.addDefaultValue(configKey.getKey(), defaultVal));

        descriptor.addConfiguration(paramDescriptor);
      }
    }
    return descriptor;
  }
}
