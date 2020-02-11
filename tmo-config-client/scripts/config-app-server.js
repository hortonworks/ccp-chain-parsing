let path = require("path");
const html = path.join(__dirname, "../dist/tmo-parser-chaining/");
const port = process.env.PORT || 4200;
const bodyParser = require("body-parser");
const compression = require("compression");
const express = require("express");

let app = express();
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
