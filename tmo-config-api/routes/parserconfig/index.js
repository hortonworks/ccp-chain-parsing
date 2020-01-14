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

GET('/parser-types', getParserTypes);

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

function getParserTypes(req, res) {
  return res.status(200).send(parserTypes);
}

module.exports = router;
