# Parser Chains

## Quick Start

1. Clone the repository locally. If you have ssh setup, run:
```bash
$ git clone git@github.com:hortonworks/ccp-tmo-search-client.git
```
Otherwise, use https:
```bash
$ git clone https://github.com/hortonworks/ccp-tmo-search-client.git
```

2. From inside the cloned directory, run the following to build a docker container. This command will take several minutes.
```bash
docker-compose up
```
3. Once you see the following message in your terminal, visit `http://localhost:4200/` in your browser.
```bash
client_1       |       You can reach our UI by using CMD + Click on the link below
client_1       |       or copying it to your browser.
client_1       |       http://localhost:4200
```

4. In order to shut down the services and remove the docker container, open a new tab and run:
```bash
docker-compose down
```
For a more detailed explination of what's going on here, see the Dockerization section below.

Contributing
============

You can close the project to your local computer via ssh with the following command:
```bash
$ git clone git@github.com:hortonworks/ccp-tmo-search-client.git
```

## With the Mock API
This scenario is good for developing the UI against backend services that do not exist yet.

Make sure you have the latest Node (LTS).

In order to run the application on your local computer, you need to run two services in parallel: the client code and the mock service (which provides mock data via http).

### Running the Mock API

```bash
$ cd frontend/parser-chains-api
```

```bash
$ npm ci
```

```bash
$ npm start
```

### Running the client application with a development server
The following spins up the client application using a development server. The dev server uses the proxy configuration set in proxy.conf.dev.json

```bash
$ cd frontend/parser-chains-client
```

```bash
$ npm ci
```

```bash
$ npm run start:dev
```

## With REST services
If you would like to develop the UI using services that currently exist, run the following:

```bash
docker-compose up
```

```bash
$ cd frontend/parser-chains-client
```

```bash
$ npm ci
```

```bash
$ npm run start:dev -- --port=4201
// because port 4200 is being used by docker
// serve the app on another port for development
```

# Dockerization and setup for docker-compose
As part of our effort to make the deployment of this software solution as easy as possible, we Dockerized all of our services and the user interface.

You can find the currently available Dockerfiles in
```./tmo-config-client/Dockerfile``` as our GUI image. And
```./backend/Dockerfile``` as a dockerized image of our main service.

To make local development, testing and, in some cases, deployment easier, there is a docker-compose.yml file provided.
This is where our services are definded and can be reconfigured, if desired.
(Right now this connection is the simplest possible: the GUI requesting a single service.)

## Running our services locally
The only thing you need to do after checking out this repo is the following:
- Ensure you have Docker installed on your machine.
- Navigate into the root directory of this repo in your terminal.
- Run ```docker-compose up```
The first time this command is run, it is going to build two images based on the latest version of our repo. This could take 2-4 minutes depending on your computer. When the build is done, it's going to fire up the required services and build up the connections between them to make the project work.

From this point, you can reach our GUI in the following URL:
```http://localhost:4200```

Right now your terminal is attached to the output of the dockerized services. This can help you see what's going on under the hood and to report issues with more detailed log messages attached.

### Shutting down our services
When you want to shut down our services, you first need to open a separate terminal window or tab (this is because the original one is now showing the output of our Dockerized services). The other way - if you are not interested in console messages - is to run Docker in detached mode by using the ```-d``` flag at startup, like:
```docker-compose up -d```

Either way, you can:
- Open a terminal
- Make sure you are in the same directory where our docker-compose.yml file located and where you fired up our services
- Run ```docker-compose down```
This will shut down our services and the blonging contiainers running in Docker.

## Rebuilding Docker images after pulling down updates from our repo
The only thing you need to do in this case is run the compose command with the ```--build``` flag:
```bash
docker-compose up --build
```

Every time you make or pull down new changes from our git repo, you should update your Docker images with the above command.
