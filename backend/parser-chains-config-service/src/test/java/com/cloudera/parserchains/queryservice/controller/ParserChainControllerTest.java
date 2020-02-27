package com.cloudera.parserchains.queryservice.controller;

import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import com.cloudera.parserchains.queryservice.model.summary.ParserChainSummary;
import com.cloudera.parserchains.queryservice.service.ChainPersistenceService;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_CREATE_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_DELETE_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_READ_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_UPDATE_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_PARSER_TEST_URL;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ParserChainControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ChainPersistenceService chainPersistenceService;
    private static int numFields = 0;
    private final String chainIdOne = "1";
    private final String chainNameOne = "chain1";

    @BeforeAll
    public static void beforeAll() {
        Method[] method = ParserChainSchema.class.getMethods();
        for (Method m : method) {
            if (m.getName().startsWith("set")) {
                numFields++;
            }
        }
    }

    @Test
    public void returns_list_of_all_chains() throws Exception {
        given(chainPersistenceService.findAll(isA(Path.class))).willReturn(
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
        given(chainPersistenceService.findAll(isA(Path.class))).willReturn(Collections.emptyList());
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
        ParserChainSchema chain = JSONUtils.INSTANCE.load(json, ParserChainSchema.class);
        ParserChainSchema expected = JSONUtils.INSTANCE.load(json, ParserChainSchema.class);
        expected.setId(chainIdOne);
        given(chainPersistenceService.create(eq(chain), isA(Path.class))).willReturn(expected);
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
        final ParserChainSchema chain = JSONUtils.INSTANCE.load(json, ParserChainSchema.class);
        given(chainPersistenceService.read(eq(chainIdOne), isA(Path.class))).willReturn(chain);
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
        given(chainPersistenceService.read(eq(chainIdOne), isA(Path.class))).willReturn(null);
        mvc.perform(
                MockMvcRequestBuilders
                        .get(API_CHAINS_READ_URL.replace("{id}", chainIdOne))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_chain_by_id_returns_updated_chain_config() throws Exception {
        String updateJson = readChainJSON.replace("{id}", chainIdOne).replace("{name}", chainNameOne);
        final ParserChainSchema updatedChain = JSONUtils.INSTANCE.load(updateJson, ParserChainSchema.class);
        given(chainPersistenceService.update(eq(chainIdOne), eq(updatedChain), isA(Path.class)))
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
        given(chainPersistenceService.update(eq(chainIdOne), isA(ParserChainSchema.class), isA(Path.class)))
                .willReturn(null);
        mvc.perform(
                MockMvcRequestBuilders
                        .get(API_CHAINS_UPDATE_URL.replace("{id}", chainIdOne))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleting_existing_chain_succeeds() throws Exception {
        given(chainPersistenceService.delete(eq(chainIdOne), isA(Path.class))).willReturn(true);
        mvc.perform(
                MockMvcRequestBuilders
                        .delete(API_CHAINS_DELETE_URL.replace("{id}", chainIdOne))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleting_nonexistent_chain_returns_not_found() throws Exception {
        given(chainPersistenceService.delete(eq(chainIdOne), isA(Path.class))).willReturn(false);
        mvc.perform(
                MockMvcRequestBuilders
                        .delete(API_CHAINS_DELETE_URL.replace("{id}", chainIdOne))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * {
     *   "sampleData": {
     *     "type": "manual",
     *     "source": "Marie, Curie"
     *   },
     *   "parserChainSchema": {
     *     "id": "3b31e549-340f-47ce-8a71-d702685137f4",
     *     "name": "My Parser Chain",
     *     "parsers": [
     *       {
     *         "id": "61e99275-e076-46b6-aaed-8acce58cc0e4",
     *         "name": "Timestamp",
     *         "type": "com.cloudera.parserchains.parsers.TimestampParser",
     *         "config": {
     *           "outputField": [
     *             {
     *               "outputField": "timestamp"
     *             }
     *           ]
     *         },
     *         "outputs": {}
     *       }
     *     ]
     *   }
     * }
     */
    @Multiline
    static String parserChainAsJson;

    @Test
    void test_parser_chain() throws Exception {
        RequestBuilder postRequest = MockMvcRequestBuilders
                .post(API_PARSER_TEST_URL)
                .content(parserChainAsJson)
                .contentType(MediaType.APPLICATION_JSON);
        mvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", instanceOf(List.class)))
                .andExpect(jsonPath("$.results", hasSize(1)))
                .andExpect(jsonPath("$.results.[0].input.original_string", is("Marie, Curie")))
                .andExpect(jsonPath("$.results.[0].output.original_string", is("Marie, Curie")))
                .andExpect(jsonPath("$.results.[0].output.timestamp", matchesPattern("[0-9]+")))
                .andExpect(jsonPath("$.results.[0].log.type", is("info")))
                .andExpect(jsonPath("$.results.[0].log.message", is("success")))
                .andExpect(jsonPath("$.results.[0].log.parserId", is("61e99275-e076-46b6-aaed-8acce58cc0e4")))
                .andReturn();
    }
}