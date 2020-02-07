var path = require("path");
const html = path.join(__dirname, "../dist/tmo-parser-chaining/");

const port = 4200;

// Express
const bodyParser = require("body-parser");
const compression = require("compression");
const express = require("express");
var app = express();

app
  .use(compression())
  .use(bodyParser.json())
  .use(express.static(html))
  .use(function(req, res) {
    res.sendFile(html + "index.html");
  })
  .listen(port, function() {
    console.log("Parser Configuration UI is listening on port " + port);
  });
