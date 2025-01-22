# FX (Foreign Exchange) Rates APIs


This project is a Spring Boot application that provides a RESTful API for working with foreign exchange (FX)
rates.
Given a currencies table and an fx_rate table, this project uses Kotlin, Spring Boot and SQL
to provide APIs to do the following:
1. Return a list of currencies available as a JSON.
2. Return an FX rate given a base currency, target currency, and a date.
If the date is not specified, the latest date from the fx_rate table is used.
3. Return a history of FX rates given a base currency, target currency, a startDate and an endDate.
4. Return part 3 as a CSV instead of a JSON.
5. Return a converted amount given a base currency, target currency, base currency amount and a date.

## Starting up the project.
1. This project uses Kotlin.
2. Docker installed and running locally.
3. Access to the Internet to download libraries used as dependencies and docker images.
4. Run " ./gradlew openApiGenerate " to generate DTOs.
5. Run the TestFxRatesBackendApplication.kt integration test to start Tomcat and PostgreSQL DB. 


Things to note:
1. The application uses [TestContainers](https://java.testcontainers.org/) to start and provide a PostgreSQL database. 
This is pre-configured and will start automatically when the application is run.
2. The application uses [Flyway](https://flywaydb.org/) to manage the database schema. The schema is defined in file
`src/main/resources/db/migration/V0001_initial_schema.sql`.
3. A sample dataset of fx rates is already defined within file `src/main/resources/db/migration/V0002__sample_fx_rates.sql`,
this provides FX rates between `2024-01-01` and `2024-01-31`.
4. The application uses [Spring JDBC](https://docs.spring.io/spring-framework/reference/data-access/jdbc.html) to 
interact with the database.
5. The application does not implement authentication.
6. The `openapi.yaml` file in the resources folder is predefined and contains the desired shape of the API.
7. The gradle build is set up to generate DTOs from the `openapi.yaml` file. This is done using the `openApiGenerate` 
gradle task. This produces the DTOs in the `build/generated/sources/openapi` folder ie
`build/generated-openapi/src/main/kotlin/com/cais/interview/fxrates/dto` folder. They are automatically added to the 
classpath.

### 1: Implementation of endpoint to get an FX rate

```http request
GET /fx-rates/{baseCurrency}/{targetCurrency}?date={date}
```

eg:
```
GET /fx-rates/USD/GBP?date=2024-01-01
```

The `date` query parameter is optional. If not specified, this should return the latest FX
rate for the given currency.

This endpoint relates to the `getLatestFXRate` operation within the `openapi.yaml`.

### 2: Implementation of endpoint to get FX rates history for a date range
 
```http request
GET /fx-rates/{baseCurrency}/{targetCurrency}/history?startDate={startDate}&endDate={endDate}
```

### 3: Enhance part 2 to support getting the response as JSON or CSV

Support returning the response as either JSON or CSV. The 
type of content to return should be determined by the `Accept` header in the request. If the `Accept` header 
contains `text/csv` then the response should be in CSV format. Otherwise, the response should be in JSON format.

The [Apache Commons CSV library](https://commons.apache.org/proper/commons-csv/) is used to generate the CSV 
response, and is on the classpath.

### 4: Implementation of endpoint to convert an amount between two currencies

```http request
GET /fx-rates/{baseCurrency}/{targetCurrency}/convert?amount={amount}&date={date}
```
The `date` query parameter is optional. If not specified, this should use the latest FX rate for the given currency.
