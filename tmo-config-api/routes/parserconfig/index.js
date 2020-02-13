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
GET('/chains/:id', getChain);

GET('/chains/:id/parsers', getParsers);
POST('/chains/:id/parsers', addParser);

GET('/parser-types', getParserTypes);

POST('/sampleparser/parsingjobs', createParseJob);

function createParseJob(req, res) {
  const sources = req.body.sampleData.source.split('\n');
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
    const parsers = req.body.chainConfig.parsers

    entry.output.ASA_TAG = asaTagRegex.exec(entry.input)[0];
    entry.output.ASA_message = asaMessageRegex.exec(entry.input)[0];
    if (log[Math.floor(Math.random() * 2)] === 'PASS') {
      entry.output.syslogMessage = syslogMessageRegex.exec(entry.input)[0];
      entry.log = {
        "type": "info",
        "message": "Parsing Successful"
      };
    } else {
      parsers
      failedParser = parsers[Math.floor(Math.random() * parsers.length)];
      entry.log = {
        "type": "error",
        "message": `Parsing Failed: ${failedParser.name} parser unable to parse.`,
        "failedParser": failedParser.id
      };
    }
  });

  let response = {
    ...req.body,
    result: {
      entries
    }
  };

  setTimeout(() => {
    res.status(200).send(response);
  }, 1000);
}

function getChains(req, res) {
  res.status(200).send(chains);
}

function getChain(req, res) {
  const chainId = req.params.id;
  const chain = chains.find((ch) => ch.id === chainId);
  if (chain) {
    res.status(200).send(chain);
    return;
  }
  res.status(404).send();
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
  const chainIndex = chains.findIndex(chain => chain.id === id);
  if (chainIndex > -1) {
    chains[chainIndex] = {
      ...chains[chainIndex],
      ...req.body
    };
    res.status(204).send();
    return;
  }
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

function getParsers(req, res) {
  const id = req.params.id;
  const chain = chains.find(chain => chain.id === id);
  if (chain) {
    res.status(200).send(chain.parsers || []);
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

function getParserTypes(req, res) {
  return res.status(200).send(parserTypes);
}

module.exports = router;
