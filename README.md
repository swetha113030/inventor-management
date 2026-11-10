# Smart Inventory Management System

A full-stack inventory management portfolio project built with **Java, Spring Boot, MySQL, SQL, Python and React**. The Spring Boot backend exposes REST APIs and manages database access, the React frontend provides an interface for inventory management, and a separate Python script consumes the API to analyse inventory and generate CSV and Excel reports.

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
-   [Database Performance Optimisation](#database-performance-optimisation)
-   [Docker and Docker Compose](#docker-and-docker-compose)
-   [React Frontend](#react-frontend)
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

### React frontend

-   Display an inventory dashboard with summary statistics.
-   List products retrieved from the Spring Boot API.
-   Add, edit and delete products through the REST API.
-   Search loaded products by name or category.
-   Display low-stock indicators.
-   Provide responsive dashboard styling.

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
React             Frontend user interface
Vite              Frontend development server and build tool
JavaScript        Frontend application logic
CSS               Responsive frontend styling

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

The React frontend and Python reporting script communicate with the Spring Boot REST API over HTTP. The controller receives requests, the service layer contains business logic, and the repository layer communicates with MySQL. Neither the React frontend nor the Python reporting script connects directly to the database.

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
|-- inventory-frontend/
|   |-- src/
|   |   |-- services/productService.js
|   |   |-- App.jsx
|   |   |-- App.css
|   |   |-- index.css
|   |   |-- main.jsx
|   |-- package.json
|   |-- vite.config.js
|
|-- docker-compose.yml
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
-   Node.js and npm.
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


## React Frontend

The React frontend is located in the `inventory-frontend/` directory. It uses
Vite for local development and communicates with the Spring Boot REST API.

### Frontend features

- Dashboard summary cards for total products, stock units, inventory value
  and low-stock product count.
- Product table populated from the backend.
- Add, edit and delete product operations.
- Client-side search by product name or category.
- Low-stock status indicators.
- Responsive layout for smaller screens.

### Configure the API proxy

The Vite development server proxies requests beginning with `/api` to the
Spring Boot backend at `http://localhost:8080`. This configuration is in
`inventory-frontend/vite.config.js`.

The frontend calls relative API paths such as `/api/products`; the browser
request is forwarded by Vite to the backend.

### Run the frontend locally

First, make sure the backend and MySQL database are running. They can be
started using Docker Compose from the project root:

```powershell
docker compose up -d
```

Open a separate PowerShell terminal and run:

```powershell
cd inventory-frontend
npm install
npm run dev
```

Open the Vite URL shown in the terminal. The default address is:

```text
http://localhost:5173/
```

Keep the Vite development server running while using the frontend.

### Build the frontend

From the `inventory-frontend/` directory, run:

```powershell
npm run build
```

Vite creates the production build in `inventory-frontend/dist/`. The generated
`node_modules/` and `dist/` directories should not be committed to Git.


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

## Database Performance Optimisation

This project uses MySQL indexes and query execution plans to investigate
and improve database query performance.

### Existing indexes

The `products` table uses indexes for commonly queried fields:

| Index | Indexed column(s) | Purpose |
|---|---|---|
| `PRIMARY` | `id` | Identifies products by their unique ID |
| `idx_products_category` | `category` | Supports category filtering |
| `idx_products_quantity` | `quantity` | Supports stock-threshold filtering |
| `idx_products_category_lower` | `LOWER(category)` | Supports the matching lower-case category expression |
| `idx_products_category_quantity` | `category`, `quantity` | Supports queries filtering by category and quantity |

The functional and composite indexes are additional indexes created during
the database optimisation exercises. Confirm that they exist in the target
database before relying on them.

### Query plan analysis

MySQL `EXPLAIN` was used to inspect how queries access the `products` table.

Examples:

```sql
-- Category lookup
EXPLAIN
SELECT *
FROM products
WHERE category = 'Electronics';

-- Low-stock lookup
EXPLAIN
SELECT *
FROM products
WHERE quantity < 5;

-- Case-insensitive category lookup
EXPLAIN
SELECT *
FROM products
WHERE LOWER(category) = LOWER('Electronics');

-- Category and quantity filtering
EXPLAIN
SELECT *
FROM products
WHERE category = 'Electronics'
  AND quantity < 5;
```

The exercises demonstrated:
- `ref` access when MySQL selected an index for an equality lookup.
- `range` access for a quantity range condition.
- A full table scan (`ALL`) for the `LOWER(category)` expression before
  creating a matching functional index.
- Use of the functional index after it was created.
- The MySQL optimiser's choice of an index for a query involving multiple
  filter conditions.

### Functional index

A functional index was created for case-insensitive category matching:

```sql
CREATE INDEX idx_products_category_lower
ON products ((LOWER(category)));
```

This index can support queries using the matching `LOWER(category)`
expression. Index usage still depends on the query expression and the
optimiser's cost estimates.

### Composite index

A composite index was created for category and quantity filtering:

```sql
CREATE INDEX idx_products_category_quantity
ON products (category, quantity);
```

The order of columns in a composite index matters. This index is intended
for queries that filter by `category`, optionally followed by a condition
on `quantity`. The optimiser may choose a different index depending on
table size, data distribution and estimated query cost.

### Performance investigation tools

- `SHOW INDEX FROM products` lists the indexes defined on the table.
- `EXPLAIN` shows the optimiser's estimated execution plan.
- `EXPLAIN ANALYZE` executes a query and reports actual execution details,
  including timing and rows processed.

For meaningful performance comparisons, test with a representative dataset.
A very small table may be faster to scan than to access through an index,
so an index is not guaranteed to be selected for every query.

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


## Docker and Docker Compose

The application runs locally using Docker Compose, with the Spring Boot REST API
and MySQL database in separate containers.

### Docker files

- `inventory-api/Dockerfile` builds the Spring Boot application using a multi-stage Maven and Java 17 build.
- `inventory-api/.dockerignore` excludes build output and local IDE files from the Docker build context.
- `docker-compose.yml` defines the API and MySQL services, ports, health check, network and persistent database volume.
- `.env` stores local database credentials and must not be committed.

### Container configuration

| Service | Container | Host port | Container port |
|---|---|---:|---:|
| Spring Boot API | `inventory-api` | `8080` | `8080` |
| MySQL | `inventory-mysql` | `3307` | `3306` |

The API connects to MySQL using the Compose service hostname `mysql` and
container port `3306`. Host port `3307` is used to avoid a conflict with a
local MySQL server using port `3306`.

### Configure environment variables

Create a `.env` file in the project root:

```properties
MYSQL_ROOT_PASSWORD=your_local_root_password
DB_PASSWORD=your_local_app_password
```

Use your own local passwords. The `.env` file must remain excluded from Git.

### Build and start the containers

Run these commands from the project root:

```powershell
docker compose up -d --build
```

Check container status:

```powershell
docker compose ps
```

View logs:

```powershell
docker compose logs -f
```

The MySQL service has a health check. Docker Compose waits for MySQL to become
healthy before starting the API service.

### Verify the Docker deployment

| Component | URL |
|---|---|
| Products API | `http://localhost:8080/api/products` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| Actuator health | `http://localhost:8080/actuator/health` |
| Actuator info | `http://localhost:8080/actuator/info` |

### Useful Docker Compose commands

```powershell
# Start containers
docker compose up -d

# Build images and start containers
docker compose up -d --build

# List containers
docker compose ps

# View all service logs
docker compose logs -f

# View API logs
docker compose logs inventory-api

# Stop and remove containers (keeps the named database volume)
docker compose down

# Rebuild images
docker compose build
```

MySQL data is stored in the named Docker volume `mysql_data`. Running
`docker compose down` does not remove this volume. Avoid `docker compose down -v`
unless you intentionally want to delete the persistent Docker database data.

### Docker learning outcomes

- Building a Spring Boot image with a multi-stage Dockerfile.
- Running MySQL and Spring Boot as separate containers.
- Configuring services with Docker Compose.
- Using environment variables for database credentials.
- Connecting containers through a Compose network.
- Persisting database data with a named volume.
- Using a health check to coordinate service startup.


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
-   Add supplier and reorder-level management.
-   Add authentication and role-based authorisation.
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
reports. - Managing source code and documentation with Git. - Building a React frontend with Vite. - Managing React state and effects with hooks. - Integrating frontend CRUD operations with REST APIs using the Fetch API. - Configuring a Vite development proxy between frontend and backend.
