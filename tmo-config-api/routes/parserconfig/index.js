const router = require('express').Router();
const crypto = require('crypto');

const GET = router.get.bind(router);
const PUT = router.put.bind(router);
const POST = router.post.bind(router);
const DELETE = router.delete.bind(router);

let chains = require('./mock-data/chains.json');

GET('/chains', getChains);
POST('/chains', createChain);
PUT('/chains/:id', updateChain);
DELETE('/chains/:id', deleteChain);

function getChains(req, res) {
  res.status(200).send(chains);
}

function createChain(req, res) {
  const id = crypto.randomBytes(8).toString('hex');
  const name = req.query.name;
  const newChain = {
    id: id,
    name: name
  }

  if (chains.find(chain => chain.name === name)) {
    res.status(409).send("This chain name already exists.");
    return;
  }

  chains = [
    ...chains,
    newChain
  ]

  res.status(201).send(chains);
}

function updateChain(req, res) {
  const id = req.params.id;
  chains.map(chain => {
    if (chain.id === id) {
      chain.name = req.query.name;
      res.status(204).send(chains);
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

module.exports = router;