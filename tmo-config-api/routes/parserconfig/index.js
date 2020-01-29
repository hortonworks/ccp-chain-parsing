const router = require('express').Router();
const crypto = require('crypto');
const uuid = require('uuid/v1');
const debug = require('debug');

const log = debug('tmo-config');

const GET = router.get.bind(router);
const PUT = router.put.bind(router);
const POST = router.post.bind(router);
const DELETE = router.delete.bind(router);

let chains = require('./mock-data/chains.json');
let parserTypes = require('./mock-data/parser-types.json');

GET('/chains', getChains);
POST('/chains', createChain);
PUT('/chains/:id', updateChain);
DELETE('/chains/:id', deleteChain);

GET('/chains/:id/parsers', getChainDetails);
POST('/chains/:id/parsers', addParser);
DELETE('/chains/:id/parsers/:parser', deleteParserFromChain);

GET('/parser-types', getParserTypes);

POST('/sampleparser/parsejobs', createParseJob);

function createParseJob(req, res) {
  const sources = req.body.sampleData.source.split(',');
  const log = ['PASS', 'FAIL'];
  let entries = sources.map(source => { return {
      input: source,
      output: {
        original_string: source
      },
      log: {}
    }
  });


  entries.map(entry => {
    const asaTagRegex = /%ASA-\d\-\d*\b/g
    const asaMessageRegex = /(?<=%ASA-\d\-\d*\:)(.*)/g
    const syslogMessageRegex = /%ASA-\d\-\d*\:(.*)/g

    entry.output.ASA_TAG = asaTagRegex.exec(entry.input)[0];
    entry.output.ASA_message = asaMessageRegex.exec(entry.input)[0];
    if (log[Math.floor(Math.random() * 2)] === 'PASS') {
      entry.output.syslogMessage = syslogMessageRegex.exec(entry.input)[0];
      entry.log = {
        "type": "info",
        "message": "Parsing Successful"
      };
    } else {
      entry.log = {
        "type": "error",
        "message": "Parsing Failed"
      };
    }
  });

  let response = {
    ...req.body,
    result: {
      entries
    }
  };
  res.status(200).send(response);
}

function getChains(req, res) {
  res.status(200).send(chains);
}

function createChain(req, res) {
  const id = crypto.randomBytes(8).toString('hex');
  const newChain = {
    ...req.body,
    id: id
  }

  if (chains.find(chain => chain.name === newChain.name)) {
    res.status(409).send("This parser chain name already exists");
    return;
  }

  chains = [
    ...chains,
    newChain
  ]

  res.status(201).send(newChain);
}

function updateChain(req, res) {
  const id = req.params.id;
  chains.map(chain => {
    if (chain.id === id) {
      chain.name = req.query.name;
      res.status(204).send(chain);
      return;
    }
  });
  res.status(404).send();
}

function deleteChain(req, res) {
  const id = req.params.id;
  if (chains.filter(chain => chain.id === id)) {
    chains = chains.filter(chain => chain.id !== id);
    res.status(200).send(chains);
    return;
  }
  res.status(404).send();
}

function getChainDetails(req, res) {
  const id = req.params.id;
  const chain = chains.find(chain => chain.id === id);
  if (chain) {
    res.status(200).send(chain);
    return;
  }
  res.status(404).send();
}

function addParser(req, res) {
  const id = req.params.id;
  const parser = req.body;
  const chain = chains.find(chain => chain.id === id);
  if (chain) {
    if (!chain.parsers) {
      chain.parsers = [];
    }
    parser.id = uuid();
    chain.parsers.push(parser);
    res.status(200).send(parser);
    return;
  }
  res.status(404).send();
}

function deleteParserFromChain(req, res) {
  const id = req.params.id;
  const parser = req.params.parser;
  let chain = chains.find(chain => chain.id === id);

  if (chain) {
    chain.parsers = chain.parsers.filter(p => p.id !== parser);
    res.status(204).send();
    return;
  }
  res.status(404).send();
}

function getParserTypes(req, res) {
  return res.status(200).send(parserTypes);
}

module.exports = router;
