package com.cloudera.parserchains.queryservice.controller;

import com.cloudera.parserchains.queryservice.config.AppProperties;
import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import com.cloudera.parserchains.queryservice.model.summary.ParserChainSummary;
import com.cloudera.parserchains.queryservice.model.exec.ParserResult;
import com.cloudera.parserchains.queryservice.model.exec.ParserResults;
import com.cloudera.parserchains.queryservice.model.exec.ParserTestRun;
import com.cloudera.parserchains.queryservice.service.ChainExecutorService;
import com.cloudera.parserchains.queryservice.service.ChainPersistenceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_CHAINS_READ_URL;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.API_PARSER_TEST;
import static com.cloudera.parserchains.queryservice.common.ApplicationConstants.PARSER_CONFIG_BASE_URL;

/**
 * The controller responsible for operations on parser chains.
 */
@RestController
@RequestMapping(value = PARSER_CONFIG_BASE_URL)
public class ChainController {

    @Autowired
    ChainPersistenceService chainPersistenceService;

    @Autowired
    ChainExecutorService chainExecutorService;

    @Autowired
    AppProperties appProperties;

    @ApiOperation(value = "Finds and returns all available parser chains.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of all parser chains.")
    })
    @GetMapping(value = API_CHAINS)
    ResponseEntity<List<ParserChainSummary>> findAll() throws IOException {
        String configPath = appProperties.getConfigPath();
        List<ParserChainSummary> configs = chainPersistenceService.findAll(Paths.get(configPath));
        return ResponseEntity.ok(configs);
    }

    @ApiOperation(value = "Creates a new parser chain.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Parser chain created successfully."),
            @ApiResponse(code = 404, message = "Unable to create a new parser chain.")
    })
    @PostMapping(value = API_CHAINS)
    ResponseEntity<ParserChainSchema> create(
            @ApiParam(name = "parserChain", value = "The parser chain to create.", required = true)
            @RequestBody ParserChainSchema chain)
            throws IOException {
        String configPath = appProperties.getConfigPath();
        ParserChainSchema createdChain = chainPersistenceService.create(chain, Paths.get(configPath));
        if (null == createdChain) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity
                    .created(URI.create(API_CHAINS_READ_URL.replace("{id}", createdChain.getId())))
                    .body(createdChain);
        }
    }

    @ApiOperation(value = "Retrieves an existing parser chain.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The parser chain with the given ID."),
            @ApiResponse(code = 404, message = "The parser chain does not exist.")
    })
    @GetMapping(value = API_CHAINS + "/{id}")
    ResponseEntity<ParserChainSchema> read(
            @ApiParam(name = "id", value = "The ID of the parser chain to retrieve.", required = true)
            @PathVariable String id)
            throws IOException {
        String configPath = appProperties.getConfigPath();
        ParserChainSchema chain = chainPersistenceService.read(id, Paths.get(configPath));
        if (null == chain) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(chain);
        }
    }

    @ApiOperation(value = "Updates an existing parser chain.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The parser chain was updated."),
            @ApiResponse(code = 404, message = "The parser chain does not exist.")
    })
    @PutMapping(value = API_CHAINS + "/{id}")
    ResponseEntity<ParserChainSchema> update(
            @ApiParam(name = "parserChain", value = "The new parser chain definition.", required = true)
            @RequestBody ParserChainSchema chain,
            @ApiParam(name = "id", value = "The ID of the parser chain to update.")
            @PathVariable String id)
            throws IOException {
        String configPath = appProperties.getConfigPath();
        try {
            ParserChainSchema updatedChain = chainPersistenceService.update(id, chain, Paths.get(configPath));
            if (null == updatedChain) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to update configuration with id=" + id);
        }
    }

    @ApiOperation(value = "Deletes a parser chain.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The parser chain was deleted."),
            @ApiResponse(code = 404, message = "The parser chain does not exist.")
    })
    @DeleteMapping(value = API_CHAINS + "/{id}")
    ResponseEntity<Void> delete(
            @ApiParam(name = "id", value = "The ID of the parser chain to delete.", required = true)
            @PathVariable String id)
            throws IOException {
        String configPath = appProperties.getConfigPath();
        if (chainPersistenceService.delete(id, Paths.get(configPath))) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "Executes a parser chain to parse sample data.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of parser results.")
    })
    @PostMapping(value = API_PARSER_TEST)
    ResponseEntity<ParserResults> test(
            @ApiParam(name = "testRun", value = "Describes the parser chain test to run.", required = true)
            @RequestBody ParserTestRun testRun) throws IOException {
        List<ParserResult> results = chainExecutorService.execute(
                testRun.getParserChainSchema(),
                testRun.getSampleData().getSource());
        return ResponseEntity.ok(new ParserResults(results));
    }
}