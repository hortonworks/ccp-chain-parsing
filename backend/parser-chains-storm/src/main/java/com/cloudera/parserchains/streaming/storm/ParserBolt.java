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

package com.cloudera.parserchains.streaming.storm;

import com.cloudera.parserchains.core.ChainBuilder;
import com.cloudera.parserchains.core.ChainLink;
import com.cloudera.parserchains.core.ChainRunner;
import com.cloudera.parserchains.core.DefaultChainBuilder;
import com.cloudera.parserchains.core.DefaultChainRunner;
import com.cloudera.parserchains.core.InvalidParserException;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.ReflectiveParserBuilder;
import com.cloudera.parserchains.core.catalog.ClassIndexParserCatalog;
import com.cloudera.parserchains.core.model.define.ParserChainSchema;
import com.cloudera.parserchains.core.utils.JSONUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserBolt extends BaseRichBolt {

  protected static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public static final String GUID = "guid";
  public static final String SUCCESS_STREAM_ID = "message";
  public static final String ERROR_STREAM_ID = "error";
  private OutputCollector collector;
  private String parserChainPath;
  private ChainLink chain;
  private ChainRunner chainRunner;

  /** Builder patterns for use in Flux **/

  public ParserBolt withParserChainPath(String parserChainPath) {
    this.parserChainPath = parserChainPath;
    return this;
  }

  @Override
  public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    this.collector = collector;
    ChainBuilder chainBuilder = new DefaultChainBuilder(new ReflectiveParserBuilder(),
        new ClassIndexParserCatalog());
    ParserChainSchema chainSchema = null;
    try {
      InputStream in = getClass().getResourceAsStream(parserChainPath);
      chainSchema = JSONUtils.INSTANCE.load(in, ParserChainSchema.class);
    } catch (IOException e) {
      throw new RuntimeException("Unable to load parser chain from file.", e);
    }
    try {
      chain = chainBuilder.build(chainSchema);
    } catch (InvalidParserException e) {
      throw new RuntimeException("Unable to build parser chain.", e);
    }
    chainRunner = new DefaultChainRunner();
  }

  @Override
  public void execute(Tuple input) {
    try {
      String inputMessage = new String(input.getBinary(0), StandardCharsets.UTF_8);
      List<Message> messages = chainRunner.run(inputMessage, chain);
      if (messages.size() > 0) {
        Message message = messages.get(messages.size() - 1);
        if (message.getError().isPresent()) {
          // TODO - figure out error format
          collector
              .emit(ERROR_STREAM_ID, input, new Values(message.getError().toString().getBytes()));
        } else {
          Map<String, String> results = message.getFields()
              .entrySet()
              .stream()
              .collect(Collectors.toMap(
                  e -> e.getKey().get(),
                  e -> e.getValue().get()));
          JSONObject ret = new JSONObject(results);
          ret.putIfAbsent(GUID, UUID.randomUUID().toString());
          collector.emit(SUCCESS_STREAM_ID, input, new Values(ret.toJSONString()));
        }
      } else {
        LOG.warn("No messages returned for tuple '{}'", input);
      }
    } catch (Throwable t) {
      // TODO - figure out error format
      // eventually need to match format for the metron error stream (see ryan's code)
      collector.emit(ERROR_STREAM_ID, input, new Values(input.toString().getBytes()));
      collector.reportError(t);
    } finally {
      collector.ack(input);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declareStream(SUCCESS_STREAM_ID, new Fields("message"));
    declarer.declareStream(ERROR_STREAM_ID, new Fields("message"));
  }
}
