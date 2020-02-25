# ccp-tmo-search-client

Contributing
============

Make sure you have the latest Node (LTS).

In order to run the application on your local computer, you need to run two services in parallel.
The client code and the mock service to get some mock data via http.

```bash
$ git clone git@github.com:hortonworks/ccp-tmo-search-client.git
```
Client Dev Server
A dev server that uses the proxy configuration set in proxy.conf.mock.json
======
```bash
$ cd tmo-config-client
```

```bash
$ npm ci
```

```bash
$ npm run start:dev
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

# Dockerization and setup for docker-compose
As part of our effort to make the deployment of this software solution as easy as possible we Dockerized all of our services and the User Interface.

You can find the currently available Dockerfiles in
```./tmo-config-client/Dockerfile``` as our GUI image. And
```./backend/Dockerfile``` as a dockerized image of our main service.

To make the local development, testing and in some cases the deployment easier we also provided a docker-compose.yml file.
This can help you to fire up our services locally and also to serve as documentation to see how they are connected.
(Right now this connection is the simplest possible: the GUI requesting a single service.)

## Running our services locally
The only thing you need to do after checking out this repo is the following:
- You need to have Docker installed on your machine
- Get into the root directory of this repo
- Run ```docker-compose up```
The first time this going to build two images based on the latest version of our repo. This could take 2-4 minutes depending on your computer. When the build is done it's going to fire up the required services and build up the connections between them to make the project work.

From this point, you can reach our GUI in the following URL:
```http://localhost:4200```

Right now your terminal is attached to the output of the dockerized services. This can help you see what's going on under the hood and to report issues with more detailed log messages attached.

### Shutting down our services
If you like to shut down our services you need to open a separate terminal (hence, the original one is now showing the output of our Dockerized services). The other way - if you are not interested in console messages - is to run Docker in detached mode by using the ```-d``` flag at startup, like:
```docker-compose up -d```

Either way, you can:
- Open a terminal
- Make sure you are in the same directory where our docker-compose.yml file located and where you fired up our services
- Run ```docker-compose down```
This will shut down our services and the blonging contiainers running in Docker.

## Rebuilding Docker images in case you pulled down updates from our repo
The only thing you need to do in this case is, in addition, the command described above is applying the ```--build``` flag like:
```docker-compose up --build```

Whit this, every time you making or pulling down new changes form our git repo you can update your Docker images.
