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

package com.cloudera.parserchains.queryservice.controller;

import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_CREATE_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_DELETE_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_READ_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_UPDATE_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_PARSER_FORM_CONFIG_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_PARSER_TYPES_URL;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cloudera.parserchains.queryservice.common.utils.CollectionsUtils;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.ParserChain;
import com.cloudera.parserchains.queryservice.model.ParserChainSummary;
import com.cloudera.parserchains.queryservice.model.ParserConfigSchema;
import com.cloudera.parserchains.queryservice.model.ParserType;
import com.cloudera.parserchains.queryservice.model.SchemaItem;
import com.cloudera.parserchains.queryservice.service.ParserConfigService;
import com.cloudera.parserchains.queryservice.service.ParserDiscoveryService;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class ParserConfigControllerTest {

  @Autowired
  private MockMvc mvc;
  @MockBean
  private ParserConfigService parserConfigService;
  @MockBean
  private ParserDiscoveryService parserDiscoveryService;
  private static int numFields = 0;
  private final String chainIdOne = "1";
  private final String chainNameOne = "chain1";

  @BeforeAll
  public static void beforeAll() {
    Method[] method = ParserChain.class.getMethods();
    for (Method m : method) {
      if (m.getName().startsWith("set")) {
        numFields++;
      }
    }
  }

  @Test
  public void returns_list_of_all_chains() throws Exception {
    given(parserConfigService.findAll(isA(Path.class))).willReturn(
        Arrays.asList(
            new ParserChainSummary().setId("1").setName("chain1"),
            new ParserChainSummary().setId("2").setName("chain2"),
            new ParserChainSummary().setId("3").setName("chain3")
        ));
    mvc.perform(MockMvcRequestBuilders.get(API_CHAINS_URL)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.*", instanceOf(List.class)))
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.[0].id", is("1")))
        .andExpect(jsonPath("$.[0].name", is("chain1")))
        .andExpect(jsonPath("$.[1].id", is("2")))
        .andExpect(jsonPath("$.[1].name", is("chain2")))
        .andExpect(jsonPath("$.[2].id", is("3")))
        .andExpect(jsonPath("$.[2].name", is("chain3")));
  }

  @Test
  public void returns_empty_list_when_no_chains() throws Exception {
    given(parserConfigService.findAll(isA(Path.class))).willReturn(Collections.emptyList());
    mvc.perform(MockMvcRequestBuilders.get(API_CHAINS_URL)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.*", instanceOf(List.class)))
        .andExpect(jsonPath("$.*", hasSize(0)));
  }

  /**
   * {
   *   "name" : "{name}"
   * }
   */
  @Multiline
  public static String createChainJSON;

  @Test
  public void creates_chain() throws Exception {
    String json = createChainJSON.replace("{name}", chainNameOne);
    ParserChain chain = JSONUtils.INSTANCE.load(json, ParserChain.class);
    ParserChain expected = JSONUtils.INSTANCE.load(json, ParserChain.class);
    expected.setId(chainIdOne);
    given(parserConfigService.create(eq(chain), isA(Path.class))).willReturn(expected);
    mvc.perform(MockMvcRequestBuilders.post(API_CHAINS_CREATE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(
            header().string(HttpHeaders.LOCATION,
                API_CHAINS_READ_URL.replace("{id}", chainIdOne)))
        .andExpect(jsonPath("$.*", hasSize(numFields)))
        .andExpect(jsonPath("$.id", is(chainIdOne)))
        .andExpect(jsonPath("$.name", is(chainNameOne)));
  }

  /**
   * {
   *   "id" : "{id}",
   *   "name" : "{name}"
   * }
   */
  @Multiline
  public static String readChainJSON;

  @Test
  public void read_chain_by_id_returns_chain_config() throws Exception {
    String json = readChainJSON.replace("{id}", chainIdOne).replace("{name}", chainNameOne);
    final ParserChain chain = JSONUtils.INSTANCE.load(json, ParserChain.class);
    given(parserConfigService.read(eq(chainIdOne), isA(Path.class))).willReturn(chain);
    mvc.perform(
        MockMvcRequestBuilders
            .get(API_CHAINS_READ_URL.replace("{id}", chainIdOne))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", is(chainIdOne)))
        .andExpect(jsonPath("$.name", is(chainNameOne)));
  }

  @Test
  public void read_chain_by_nonexistent_id_returns_not_found() throws Exception {
    given(parserConfigService.read(eq(chainIdOne), isA(Path.class))).willReturn(null);
    mvc.perform(
        MockMvcRequestBuilders
            .get(API_CHAINS_READ_URL.replace("{id}", chainIdOne))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void update_chain_by_id_returns_updated_chain_config() throws Exception {
    String updateJson = readChainJSON.replace("{id}", chainIdOne).replace("{name}", chainNameOne);
    final ParserChain updatedChain = JSONUtils.INSTANCE.load(updateJson, ParserChain.class);
    given(parserConfigService.update(eq(chainIdOne), eq(updatedChain), isA(Path.class)))
        .willReturn(updatedChain);
    mvc.perform(MockMvcRequestBuilders
        .put(API_CHAINS_UPDATE_URL.replace("{id}", chainIdOne))
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateJson))
        .andExpect(status().isNoContent());
  }

  @Test
  public void update_chain_by_nonexistent_id_returns_not_found() throws Exception {
    given(parserConfigService.update(eq(chainIdOne), isA(ParserChain.class), isA(Path.class)))
        .willReturn(null);
    mvc.perform(
        MockMvcRequestBuilders
            .get(API_CHAINS_UPDATE_URL.replace("{id}", chainIdOne))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void deleting_existing_chain_succeeds() throws Exception {
    given(parserConfigService.delete(eq(chainIdOne), isA(Path.class))).willReturn(true);
    mvc.perform(
        MockMvcRequestBuilders
            .delete(API_CHAINS_DELETE_URL.replace("{id}", chainIdOne))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  public void deleting_nonexistent_chain_returns_not_found() throws Exception {
    given(parserConfigService.delete(eq(chainIdOne), isA(Path.class))).willReturn(false);
    mvc.perform(
        MockMvcRequestBuilders
            .delete(API_CHAINS_DELETE_URL.replace("{id}", chainIdOne))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void returns_list_of_all_parser_types() throws Exception {
    given(parserDiscoveryService.findAll()).willReturn(
        Arrays.asList(
            new ParserType().setId("1").setName("type1"),
            new ParserType().setId("2").setName("type2"),
            new ParserType().setId("3").setName("type3")
        ));
    mvc.perform(MockMvcRequestBuilders.get(API_PARSER_TYPES_URL)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.*", instanceOf(List.class)))
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.[0].id", is("1")))
        .andExpect(jsonPath("$.[0].name", is("type1")))
        .andExpect(jsonPath("$.[1].id", is("2")))
        .andExpect(jsonPath("$.[1].name", is("type2")))
        .andExpect(jsonPath("$.[2].id", is("3")))
        .andExpect(jsonPath("$.[2].name", is("type3")));
  }

  @Test
  public void returns_list_of_all_parser_type_config_schemas() throws Exception {
    SchemaItem fieldOne = new SchemaItem()
        .setName("firstField")
        .setDescription("First field description")
        .setLabel("first field")
        .setPath("config")
        .setRequired("true")
        .setType("text");
    SchemaItem fieldTwo = new SchemaItem()
        .setName("secondField")
        .setDescription("Second field description")
        .setLabel("second field")
        .setPath("config")
        .setRequired("true")
        .setType("text");
    given(parserDiscoveryService.findAllConfig()).willReturn(
        CollectionsUtils.toMap(
            "type1", new ParserConfigSchema()
                .setId("1")
                .addSchemaItem(fieldOne)
                .addSchemaItem(fieldTwo),
            "type2", new ParserConfigSchema()
                .setId("2")
                .addSchemaItem(fieldOne)
                .addSchemaItem(fieldTwo),
            "type3", new ParserConfigSchema()
                .setId("3")
                .addSchemaItem(fieldOne)
                .addSchemaItem(fieldTwo)
        ));
    mvc.perform(MockMvcRequestBuilders.get(API_PARSER_FORM_CONFIG_URL)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.*", instanceOf(List.class)))
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.type1.id", is("1")))
        .andExpect(jsonPath("$.type1.schemaItems[0].name", is("firstField")))
        .andExpect(jsonPath("$.type2.id", is("2")))
        .andExpect(jsonPath("$.type2.schemaItems[0].name", is("firstField")))
        .andExpect(jsonPath("$.type3.id", is("3")))
        .andExpect(jsonPath("$.type3.schemaItems[0].name", is("firstField")));
  }
}
