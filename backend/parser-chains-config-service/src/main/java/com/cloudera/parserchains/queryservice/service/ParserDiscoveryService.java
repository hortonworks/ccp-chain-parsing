package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.queryservice.model.ParserConfigSchema;
import com.cloudera.parserchains.queryservice.model.ParserResults;
import com.cloudera.parserchains.queryservice.model.ParserType;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ParserDiscoveryService {

  List<ParserType> findAll() throws IOException;

  ParserConfigSchema read(String type) throws IOException;

  Map<String, ParserConfigSchema> findAllConfig() throws IOException;

  // TODO not sure if the live view tests will be run through the entire chain or a single parser?
  ParserResults test(String type, String message) throws IOException;
}
