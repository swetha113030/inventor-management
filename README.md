# Smart Inventory Management System

A portfolio project demonstrating how to build an inventory management
backend with **Java, Spring Boot, MySQL, SQL and Python**. The Spring
Boot application exposes REST APIs and manages database access, while a
separate Python script consumes the API to analyse inventory and
generate CSV and Excel reports.

## Table of Contents

-   [Project Overview](#project-overview)
-   [Features](#features)
-   [Technology Stack](#technology-stack)
-   [Architecture](#architecture)
-   [Project Structure](#project-structure)
-   [Prerequisites](#prerequisites)
-   [Getting Started](#getting-started)
-   [REST API Reference](#rest-api-reference)
-   [Python Inventory Reports](#python-inventory-reports)
-   [SQL and Database Optimisation](#sql-and-database-optimisation)
-   [Running Tests](#running-tests)
-   [Logging and Monitoring](#logging-and-monitoring)
-   [Troubleshooting](#troubleshooting)
-   [Future Enhancements](#future-enhancements)

## Project Overview

The Smart Inventory Management System provides a way to manage product
records and analyse stock information.

The Java backend handles product operations, validation, business logic
and database communication. Python consumes the backend's REST API and
produces inventory analysis reports.

**Design principle:** Python does not connect directly to MySQL. It
communicates with the Spring Boot API, keeping database access and
application logic in the backend.

## Features

### Inventory management

-   Create, retrieve, update and delete product records.
-   Retrieve an individual product by its ID.
-   Search and filter product records.
-   Validate product names, categories, prices and quantities.
-   Return useful error responses for invalid requests or missing
    records.

### Inventory analytics

-   Calculate total inventory value, product count and stock units.
-   Identify products below a configurable stock threshold.
-   Retrieve products by category.
-   Generate an inventory summary.

### Database and API

-   Persist product information in MySQL using Spring Data JPA and
    Hibernate.
-   Use custom JPQL queries for aggregate calculations.
-   Use database indexes for frequently queried fields.
-   Support pagination and sorting.

### Python reporting

-   Fetch product data from the Spring Boot REST API.
-   Analyse inventory data with Pandas.
-   Calculate each product's inventory value.
-   Identify low-stock products.
-   Export reports to CSV and Excel.

## Technology Stack

Technology        Purpose
  ----------------- ----------------------------------------------
Java              Backend programming language
Spring Boot       REST API and application framework
Spring Data JPA   Repository and database access
Hibernate         Object-relational mapping
MySQL             Relational database
SQL / JPQL        Data retrieval, aggregation and optimisation
Maven             Java dependency and build management
Python            Reporting and data-processing scripts
Requests          Calling the Spring Boot API from Python
Pandas            Data analysis and report preparation
OpenPyXL          Excel file generation

## Architecture

``` text
                  +-------------------------+
                  |     Python Reporting    |
                  | Requests + Pandas       |
                  | CSV / Excel generation  |
                  +------------+------------+
                               |
                               | HTTP / JSON
                               v
                  +-------------------------+
                  |    Spring Boot REST API |
                  | Controllers             |
                  | Services                |
                  | Repositories            |
                  +------------+------------+
                               |
                               | Spring Data JPA
                               v
                  +-------------------------+
                  |       MySQL Database    |
                  |       inventory_db      |
                  +-------------------------+
```

The controller receives HTTP requests, the service layer contains
business logic, and the repository layer communicates with MySQL. The
Python reporting script calls the API rather than querying the database
directly.

## Project Structure

``` text
java-python-sql-inventory-management/
|
|-- inventory-api/
|   |-- src/
|   |   |-- main/
|   |   |   |-- java/com/example/inventory/
|   |   |   |   |-- controller/
|   |   |   |   |-- service/
|   |   |   |   |-- repository/
|   |   |   |   |-- entity/
|   |   |   |   |-- dto/
|   |   |   |   |-- GlobalExceptionHandler.java
|   |   |   |   |-- InventoryApiApplication.java
|   |   |   |
|   |   |   |-- resources/
|   |   |       |-- application.properties
|   |   |
|   |   |-- test/
|   |
|   |-- pom.xml
|
|-- python-tools/
|   |-- inventory_report.py
|   |-- requirements.txt
|   |-- inventory_report.csv       # Generated report
|   |-- inventory_report.xlsx     # Generated report
|
|-- sql/
|   |-- schema.sql
|   |-- sample_data.sql
|   |-- inventory_reports.sql
|
|-- .gitignore
|-- README.md
```

The Java package layout above describes the main application layers.
Generated reports and the Python virtual environment are local outputs
and should not be committed.

## Prerequisites

-   Java 17 or later.
-   MySQL Server and MySQL Workbench.
-   Python 3.
-   IntelliJ IDEA or another Java IDE.
-   Git.
-   Maven, unless a Maven wrapper is included in the project.

## Getting Started

### 1. Create the MySQL database

1.  Start MySQL Server.
2.  Open MySQL Workbench and connect to your local MySQL instance.
3.  Execute `sql/schema.sql`.
4.  Execute `sql/sample_data.sql` to insert sample products.

The sample-data script is intended for initial setup. Running it
repeatedly may insert duplicate sample records.

### 2. Configure database credentials

The Spring Boot application reads its database settings from environment
variables.

  --------------------------------------------------------------------------------------------
Variable                Purpose                 Default
  ----------------------- ----------------------- --------------------------------------------
`DB_URL`                MySQL JDBC connection   `jdbc:mysql://localhost:3306/inventory_db`
URL

`DB_USERNAME`           MySQL username          `root`

`DB_PASSWORD`           MySQL password          Must be configured locally
--------------------------------------------------------------------------------------------

For local development in Windows PowerShell, set the variables before
starting the application:

``` powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/inventory_db"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-mysql-password"
```

Replace the example password with your local MySQL password. Do not
commit real credentials, passwords or tokens to GitHub.

Alternatively, configure these environment variables in the IntelliJ Run
Configuration for `InventoryApiApplication`.

### 3. Start the Spring Boot API

Open the project in IntelliJ IDEA and run `InventoryApiApplication.java`
from the `inventory-api` module.

If Maven is installed, you can also start the application from the
`inventory-api` directory:

``` bash
mvn spring-boot:run
```

When startup succeeds, the API is available at `http://localhost:8080`.

Keep the API running while using the Python reporting script.

### 4. Verify the API

Open this address in a browser:

``` text
http://localhost:8080/api/products
```

A successful response should contain product data in JSON format.

You can also use:

``` bash
curl http://localhost:8080/api/products
```

### 5. Set up and run the Python report

Open a separate terminal in the project root.

Create a virtual environment:

``` bash
python -m venv python-tools/.venv
```

Activate it on Windows PowerShell:

``` powershell
.\python-tools\.venv\Scripts\Activate.ps1
```

Install the dependencies:

``` bash
python -m pip install -r python-tools/requirements.txt
```

Run the report while the Spring Boot API is running:

``` bash
python python-tools/inventory_report.py
```

The script fetches product data from the API and generates: -
`python-tools/inventory_report.csv` -
`python-tools/inventory_report.xlsx`

The generated files are local outputs and are excluded from Git.

## REST API Reference

The following endpoints are documented for the inventory API.

  ------------------------------------------------------------------------------------------------------------
Method                  Endpoint                                                     Purpose
  ----------------------- ------------------------------------------------------------ -----------------------
GET                     `/api/products`                                              Retrieve all products

GET                     `/api/products/{id}`                                         Retrieve a product by
ID

POST                    `/api/products`                                              Create a product

PUT                     `/api/products/{id}`                                         Update a product

DELETE                  `/api/products/{id}`                                         Delete a product

GET                     `/api/products/search?name=mouse`                            Search products by name

GET                     `/api/products/low-stock?threshold=5`                        Find products below a
stock threshold

GET                     `/api/products/inventory-value`                              Calculate total
inventory value

GET                     `/api/products/count`                                        Count products

GET                     `/api/products/total-stock`                                  Calculate total stock
units

GET                     `/api/products/category/{category}`                          Retrieve products by
category

GET                     `/api/products/category/{category}/count`                    Count products in a
category

GET                     `/api/products/inventory-summary`                            Retrieve inventory
summary

GET                     `/api/products/page?page=0&size=5&sortBy=id&direction=asc`   Retrieve a page of
products with sorting
  ------------------------------------------------------------------------------------------------------------

Pagination parameters: - `page`: zero-based page number. - `size`:
number of records per page. - `sortBy`: product field used for
sorting. - `direction`: `asc` or `desc`.

### Example: Create a product

Example JSON request body:

``` json
{
  "name": "Wireless Mouse",
  "category": "Accessories",
  "price": 19.99,
  "quantity": 20
}
```

Example PowerShell request:

``` powershell
$body = @{
    name = "Wireless Mouse"
    category = "Accessories"
    price = 19.99
    quantity = 20
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -ContentType "application/json" -Body $body
```

The API validates incoming product data. For example, product names and
categories cannot be blank, prices must be greater than zero, and
quantities cannot be negative.

## Python Inventory Reports

The Python script calls:

``` text
GET http://localhost:8080/api/products
```

It uses Pandas to process the returned JSON data and calculate: - Number
of products. - Total stock units. - Inventory value
(`price × quantity`). - Products below the configured low-stock
threshold.

It exports product data, including calculated inventory values, to CSV
and Excel files.

**Important:** Start the Spring Boot API before running the Python
script. The script depends on the API being available.

## SQL and Database Optimisation

The `sql/` directory contains database setup and SQL practice scripts.

The project uses SQL and JPQL for: - Counting products. - Summing stock
quantities. - Calculating total inventory value. - Grouping inventory
value by category. - Filtering products by stock quantity.

Indexes are created for commonly queried product fields such as category
and quantity. Use `EXPLAIN` in MySQL Workbench to inspect query
execution.

Example:

``` sql
EXPLAIN
SELECT *
FROM products
WHERE category = 'Accessories';
```

## Running Tests

The project includes Spring Boot test dependencies and an H2 in-memory
database dependency for isolated testing.

From the `inventory-api` directory, run:

``` bash
mvn test
```

Review the Maven test output to confirm the test results. The H2 test
database is separate from the local MySQL development database.

## Logging and Monitoring

The application uses **SLF4J** with Spring Boot's default logging implementation
to record application activity and support troubleshooting.

### Application logging

The `ProductService` uses a class-level SLF4J logger to record important
operations, including:

- Retrieving all products and individual products.
- Creating, updating and deleting products.
- Calculating inventory value and stock totals.
- Finding low-stock products.
- Generating inventory summaries.
- Retrieving paginated product results.

Example log messages:

```text
INFO  - Fetching all products
INFO  - Successfully retrieved 6 products
INFO  - Adding new product: Gaming Mouse
INFO  - Product created successfully with ID: 10
WARN  - Product not found with ID: 100
```

### Logging configuration

Logging levels and the console output format are configured in
`inventory-api/src/main/resources/application.properties`:

```properties
logging.level.root=INFO
logging.level.com.example.inventory=DEBUG
logging.level.org.springframework.web=WARN
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n
```

| Log level | Purpose |
|---|---|
| `INFO` | Records normal application activity |
| `DEBUG` | Provides detailed diagnostic information |
| `WARN` | Highlights unexpected or noteworthy conditions |
| `ERROR` | Records application errors |

### Spring Boot Actuator

Spring Boot Actuator provides endpoints for checking application health
and inspecting application information and metrics.

The following configuration exposes the endpoints:

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

management.info.env.enabled=true
info.app.name=Smart Inventory Management System
info.app.description=Inventory management REST API built with Spring Boot
info.app.version=1.0.0
```

When the application is running on port `8080`, the monitoring endpoints
are available at:

| Endpoint | Description |
|---|---|
| `http://localhost:8080/actuator/health` | Reports application and configured component health |
| `http://localhost:8080/actuator/info` | Displays the configured application information |
| `http://localhost:8080/actuator/metrics` | Lists available application metrics |

A healthy application should report a status such as `UP` from the health
endpoint. The exact health response depends on the configured health
indicators and the state of their underlying components.

## Troubleshooting

  ---------------------------------------------------------------------------
Problem                             What to check
  ----------------------------------- ---------------------------------------
MySQL access denied                 Check `DB_USERNAME` and `DB_PASSWORD`.

Cannot connect to MySQL             Confirm MySQL Server is running and
`DB_URL` is correct.

Table does not exist                Run `sql/schema.sql` and then
`sql/sample_data.sql`.

Port 8080 is already in use         Stop the other process using the port
or change `server.port`.

Python cannot connect to the API    Start Spring Boot and check
`http://localhost:8080/api/products`.

Python package is missing           Activate the virtual environment and
install `requirements.txt`.

Maven dependency or build error     Check the internet connection and Maven
output.

Database password is unavailable    Set `DB_PASSWORD` in the terminal or
IntelliJ Run Configuration used to
start the application.
  ---------------------------------------------------------------------------

## Future Enhancements

-   Expand automated unit and integration test coverage as new features are added.
-   Expand Swagger/OpenAPI documentation with additional response examples.
-   Add Docker and Docker Compose for repeatable local setup.
-   Add supplier and reorder-level management.
-   Add authentication and role-based authorisation.
-   Build a web frontend for inventory users.
-   Add CI using GitHub Actions.
-   Deploy the application to a cloud platform.

## Project Learning Outcomes

This project provides practical experience with: - Building REST APIs
using Java and Spring Boot. - Separating controller, service and
repository responsibilities. - Mapping Java entities to relational
database tables with JPA and Hibernate. - Validating request data and
handling API errors. - Writing SQL and JPQL aggregate queries. -
Applying database indexes, pagination and sorting. - Consuming REST APIs
from Python. - Analysing data with Pandas and exporting CSV and Excel
reports. - Managing source code and documentation with Git.
