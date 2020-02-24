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
import com.cloudera.parserchains.core.catalog.ParserCatalog;
import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.queryservice.model.ParserConfigSchema;
import com.cloudera.parserchains.queryservice.model.ParserResults;
import com.cloudera.parserchains.queryservice.model.ParserType;
import com.cloudera.parserchains.queryservice.model.SchemaItem;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultParserDiscoveryService implements ParserDiscoveryService {

  @Autowired
  private ParserCatalog catalog;

  @Autowired
  public DefaultParserDiscoveryService(ParserCatalog catalog) {
    this.catalog = catalog;
  }

  @Override
  public List<ParserType> findAll() throws IOException {
    return infoToType(catalog.getParsers());
  }

  public static List<ParserType> infoToType(List<ParserInfo> parserInfos) {
    return parserInfos.stream()
        .map(info -> new ParserType()
            .setId(info.getName())
            .setName(info.getName())
        ).collect(Collectors.toList());
  }

  @Override
  public ParserConfigSchema read(String type) throws IOException {
    return null;
  }

  @Override
  public Map<String, ParserConfigSchema> findAllConfig() throws IOException {
    return getConfigs();
//    return infoToConfig(catalog.getParsers());
  }

  @Override
  public ParserResults test(String type, String message) throws IOException {
    // TODO - this is for running data through a parser chain
    return null;
  }

  // TODO replace this with a real implementation
  private Map<String, ParserConfigSchema> getConfigs() {
    Map<String, ParserConfigSchema> configMap = new HashMap<>();
    configMap.put("Syslog",
        new ParserConfigSchema()
            .setId("Syslog")
            .addSchemaItem(new SchemaItem()
                .setName("firstField")
                .setDescription("First field description")
                .setLabel("first field")
                .setPath("config")
                .setRequired("true")
                .setType("text"))
            .addSchemaItem(new SchemaItem()
                .setName("secondField")
                .setDescription("Second field description")
                .setLabel("second field")
                .setPath("config")
                .setRequired("true")
                .setType("text")
            ));
    configMap.put("Timestamp",
        new ParserConfigSchema()
            .setId("Timestamp")
            .addSchemaItem(new SchemaItem()
                .setName("firstField")
                .setDescription("First field description")
                .setLabel("first field")
                .setPath("config")
                .setRequired("true")
                .setType("text"))
            .addSchemaItem(new SchemaItem()
                .setName("secondField")
                .setDescription("Second field description")
                .setLabel("second field")
                .setPath("config")
                .setRequired("true")
                .setType("text")
            ));
    return configMap;
  }

  /*
  TODO
   */
  public static Map<String, ParserConfigSchema> infoToConfig(List<ParserInfo> parserInfos) {
    Map<String, ParserConfigSchema> configMap = new HashMap<>();
    for (ParserInfo info : parserInfos) {
      try {
        Constructor<Parser> constructor = info.getParserClass().getConstructor();
        Parser parser = constructor.newInstance();
        List<ConfigDescriptor> configDescriptors = parser.validConfigurations();
        for (ConfigDescriptor desc : configDescriptors) {
          // TODO - need to figure out mapping from descriptor -> required values -> Tamas's model
          /*
          Map<ConfigKey, ConfigDescription> requiredValues = desc.getRequiredValues();
          ParserFormConfig config = new ParserFormConfig().
           */
        }
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
        throw new RuntimeException("Unable to create parser", e);
      }
    }
//    return parserInfos.stream().collect(Collectors.toMap(ParserInfo::getName,
//        info -> new ParserFormConfig().setId(info.getName()).setName(info.getName())));
    return null;
  }

}
