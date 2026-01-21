# DTU Pay - Group 23 (02267 Winter 2026)
DTU Pay is a mobile payment solution that enables customers and merchants to conduct transactions using token-based payments. Users sign up for DTU Pay using their [FastMoney Bank](http://fm-00.compute.dtu.dk/) account. 

## Getting started
A `build_and_run.sh` script is provided in the project root. 


```sh
chmod +x build_and_run.sh  # make sure script is executable
./build_and_run.sh
```

This script will build the services individually, including performing individual service tests, and deploy the system locally. Once the services are up, the script will run the end-to-end tests using the test client.

## Architecture
The system is built using an event-driven microservices architecture with an external-facing RESTful API with three ports, one for each type of user (customer, merchant and manager). Internally, services communicate asynchronously with message-based communication using RabbitMQ. Each service is containerized using Docker and orchestrated with Docker Compose.

The system consists of an *account service*, a *token service*, a *payment service* and a *reporting service*, as well as the RabbitMQ instance and a facade. These services run on a Docker network with only the facade externally exposing the REST HTTP service on port 8080.

DTU Pay relies on the external FastMoney Bank for conducting transactions. The payment service interacts with FastMoney using the provided SOAP interface.


## Group members
This project was developed by group 23 consisting of the following group members:

- Jeppe Bonde Weikop (s253874)
- Mathias Lindeloff (s215698)
- Jonas Høyer (s253872)
- Tor Carlos Høydahl Ohme (s253037)
- Laura Vieira Teixeira (s243019)
- William A. Carlsen (s214881)

