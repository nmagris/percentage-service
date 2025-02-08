# Challenge Tenpo

### API Description:

This project provides a REST API built with Spring Boot and Java 21. The API calculates the sum
of two numbers and applies a percentage obtained from an external service to generate the final
result. The percentage value is cached in a distributed Redis instance to ensure high availability
and low latency, making it accessible across all API instances.

The API also includes a request logging mechanism with persistence, allowing users to query the
call history through a paginated endpoint. To maintain system stability, a strict rate limit of 3
requests per minute (RPM) is enforced.

For testing purposes, a mock service named [Mocky](https://run.mocky.io) is used as the external
percentage provider.

### Before starting

Before running the project, make sure you have the following installed:

* Docker Desktop: Download it from [Docker](https://www.docker.com/)
* Postman: Import the `tenpo.postman_collection.json` file located in the `percentage-service`
  folder into your local Postman application.

### Getting Started

To start the project, execute the following commands in the project directory:

* `docker-compose up -d`

### Using the API

The Postman collection contains three predefined requests for testing the API:

#### 1) GET percentage

Calculates the sum of two numbers (`first_number` and `second_number` passed as query parameters)
and applies a percentage obtained from an external
service [Mocky Endpoint](https://run.mocky.io/v3/9a90b758-6b59-4748-9539-37353d5a2183).

#### 2) GET percentage test

Similar to the previous endpoint, but allows dynamic testing by providing a custom mock endpoint
(`mocked_endpoint`) as a query parameter. This is useful for verifying the behavior of cached
percentage values.

Two predefined mock endpoints are available for testing:

* [Mocky Endpoint that returns 10](https://run.mocky.io/v3/9a90b758-6b59-4748-9539-37353d5a2183).
* [Mocky Endpoint that returns 20](https://run.mocky.io/v3/b1cc418a-cdbc-462b-a8f0-30f7f6176a9b).

For failure simulation, you can use this mock endpoint:

* [Mocky Endpoint that fails](https://run.mocky.io/v3/b1cc418a-cdbc-462b-a8f0-30f7f6176a9b-fails).

#### 3) GET api calls

Retrieves a list of all API calls made to the `/percentage/calculated-value` endpoint, sorted by the
most recent
first.
This endpoint supports pagination for easy navigation through the request history.
