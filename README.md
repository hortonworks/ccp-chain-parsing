# ccp-tmo-search-client

Contributing
============

Make sure you have the latest Node (LTS). 

In order to run the application on your local computer, you need to run two services in parallel. 
The client code and the mock service to get some mock data via http.

```bash
$ git clone git@github.com:hortonworks/ccp-tmo-search-client.git
```

Client
======

```bash
$ cd tmo-config-client
```

```bash
$ npm ci
```

```bash
$ npm start
```

Mock API
========

```bash
$ cd tmo-config-api
```

```bash
$ npm ci
```

```bash
$ npm start
```
