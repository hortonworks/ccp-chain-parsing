# Parser Chains

## Quick Start

1. From the project root, run the following to build a docker container. This command will take several minutes when run for the first time.
    ```bash
    docker-compose up --build
    ```
1. Once you see the following message in your terminal, visit `http://localhost:4200/` in your browser.
    ```bash
    client_1       |       You can reach our UI by using CMD + Click on the link below
    client_1       |       or copying it to your browser.
    client_1       |       http://localhost:4200
    ```

1. In order to shut down the services and remove the docker container, open a new tab and run:
    ```bash
    docker-compose down
    ```
