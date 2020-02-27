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
import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.queryservice.model.ParserID;
import com.cloudera.parserchains.queryservice.model.describe.ConfigParamDescriptor;
import com.cloudera.parserchains.queryservice.model.describe.ParserDescriptor;
import com.cloudera.parserchains.queryservice.model.summary.ObjectMapper;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummary;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummaryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cloudera.parserchains.queryservice.service.DefaultParserDiscoveryService.DEFAULT_PATH_ROOT;
import static com.cloudera.parserchains.queryservice.service.DefaultParserDiscoveryService.DEFAULT_SCHEMA_TYPE;
import static com.cloudera.parserchains.queryservice.service.DefaultParserDiscoveryService.PATH_DELIMITER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultParserDiscoveryServiceTest {
  @Mock private ParserCatalog catalog;
  @Mock private ParserBuilder builder;
  @Mock private Parser parser1;
  @Mock private Parser parser2;
  private ParserInfo parserInfo1;
  private ParserInfo parserInfo2;
  private ConfigDescriptor descriptor1;
  private ParserDiscoveryService service;
  private ObjectMapper<ParserSummary, ParserInfo> mapper;

  @BeforeEach
  public void beforeEach() throws IOException {
    mapper = new ParserSummaryMapper();
    service = new DefaultParserDiscoveryService(catalog, builder, mapper);
    parserInfo1 = ParserInfo.builder()
            .name("type1")
            .description("type 1 description")
            .parserClass(parser1.getClass())
            .build();
    parserInfo2 = ParserInfo.builder()
            .name("type2")
            .description("type 2 description")
            .parserClass(parser2.getClass())
            .build();
    descriptor1 = ConfigDescriptor.builder()
            .name("outputField")
            .description("The name of the output field.")
            .isRequired(true)
            .acceptsValue("outputField", "The name of the output field.")
            .build();
  }

  private void setupCatalog(List<ParserInfo> parserInfos) {
    when(catalog.getParsers())
            .thenReturn(parserInfos);
  }

  private void setupParser(Parser parser, ParserInfo parserInfo1, ConfigDescriptor descriptor) {
    // the parser needs to return the given descriptors
    when(parser.validConfigurations())
            .thenReturn(Arrays.asList(descriptor));

    // the builder needs to return the parser
    when(builder.build(eq(parserInfo1)))
            .thenReturn(parser);
  }

  @Test
  void findAll() throws IOException {
    // the catalog needs to include parser1
    setupCatalog(Arrays.asList(parserInfo1, parserInfo2));

    // execute - find all parsers
    List<ParserSummary> actual = service.findAll();
    List<ParserSummary> expected = catalog.getParsers()
            .stream()
            .map(info -> mapper.reform(info))
            .collect(Collectors.toList());
    assertThat(actual, equalTo(expected));
    assertThat(actual.size(), equalTo(2));
  }

  @Test
  void describe() throws IOException {
    // setup
    setupParser(parser1, parserInfo1, descriptor1);
    setupCatalog(Arrays.asList(parserInfo1));

    // execute - describe the parameters exposed by parser1
    ParserID parserID = ParserID.of(parser1.getClass());
    ParserDescriptor schema = service.describe(parserID);

    // validate
    assertThat("The parserID should be set to the parser's class name.",
            schema.getParserName().getName(), equalTo(parserInfo1.getName()));
    ConfigParamDescriptor expectedItem = new ConfigParamDescriptor()
            .setName(descriptor1.getName().get())
            .setDescription(descriptor1.getDescription().get())
            .setLabel(descriptor1.getDescription().get())
            .setType(DEFAULT_SCHEMA_TYPE)
            .setRequired(Boolean.toString(descriptor1.isRequired()))
            .setPath(DEFAULT_PATH_ROOT);
    assertThat("Expect the schema item to match the descriptor.",
            schema.getConfigurations().get(0), equalTo(expectedItem));
  }

  @Test
  void describeMultipleValues() throws IOException {
    // setup
    ConfigDescriptor descriptor = ConfigDescriptor.builder()
            .name("fieldToRename")
            .description("Field to Rename")
            .isRequired(true)
            .acceptsValue("from", "The original name of the field to rename.")
            .acceptsValue("to", "The new name of the field.")
            .build();
    setupParser(parser1, parserInfo1, descriptor);
    setupCatalog(Arrays.asList(parserInfo1));

    // execute - describe the parameters exposed by parser1
    ParserID parserID = ParserID.of(parserInfo1.getParserClass());
    ParserDescriptor schema = service.describe(parserID);

    // validate
    assertThat("Expect 1 schema item for each accepted value; from/to in this case.",
            schema.getConfigurations().size(), equalTo(2));
    ConfigParamDescriptor expectedFromItem = new ConfigParamDescriptor()
            .setName("from")
            .setDescription("The original name of the field to rename.")
            .setLabel("Field to Rename")
            .setType(DEFAULT_SCHEMA_TYPE)
            .setRequired(Boolean.toString(descriptor.isRequired()))
            .setPath(DEFAULT_PATH_ROOT + PATH_DELIMITER + "fieldToRename");
    ConfigParamDescriptor expectedToItem = new ConfigParamDescriptor()
            .setName("to")
            .setDescription("The new name of the field.")
            .setLabel("Field to Rename")
            .setType(DEFAULT_SCHEMA_TYPE)
            .setRequired(Boolean.toString(descriptor.isRequired()))
            .setPath(DEFAULT_PATH_ROOT + PATH_DELIMITER + "fieldToRename");
    assertThat("Expected the schema items to describe the 'to' field.",
            schema.getConfigurations(), hasItem(expectedToItem));
    assertThat("Expected the schema items to describe the 'from' field.",
            schema.getConfigurations(), hasItem(expectedFromItem));
  }

  @Test
  void describeAll() throws IOException {
    // setup
    setupParser(parser1, parserInfo1, descriptor1);
    setupCatalog(Arrays.asList(parserInfo1));

    // execute - describe all available parser
    Map<ParserID, ParserDescriptor> schema = service.describeAll();

    // validate
    assertThat("Expect 1 parser type to be returned.",
            schema.size(), equalTo(1));
    ParserID expected = mapper.reform(parserInfo1).getId();
    assertThat("Expect the result to be keyed by the parser ID.",
            schema.containsKey(expected));
    ParserDescriptor actual = schema.get(expected);
    ConfigParamDescriptor expectedItem = new ConfigParamDescriptor()
            .setName(descriptor1.getName().get())
            .setDescription(descriptor1.getDescription().get())
            .setLabel(descriptor1.getDescription().get())
            .setType(DEFAULT_SCHEMA_TYPE)
            .setRequired(Boolean.toString(descriptor1.isRequired()))
            .setPath(DEFAULT_PATH_ROOT);
    assertThat("Expect the schema item to match the descriptor.",
            actual.getConfigurations().get(0), equalTo(expectedItem));
  }
}
