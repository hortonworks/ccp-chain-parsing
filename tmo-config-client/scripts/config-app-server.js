if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}

let path = require("path");
const HTML = path.join(__dirname, "../dist/tmo-parser-chaining/");
const PORT = process.env.PORT || 4200;
const BODY_PARSER = require("body-parser");
const COMPRESSION = require("compression");
const EXPRESS = require("express");

let app = EXPRESS();
app
  .use(COMPRESSION())
  .use(BODY_PARSER.json())
  .use(EXPRESS.static(HTML))
  .use(function(req, res) {
    res.sendFile(HTML + "index.html");
  })
  .listen(PORT, function() {
    console.log("Parser Configuration UI is listening on port " + PORT);
  });
