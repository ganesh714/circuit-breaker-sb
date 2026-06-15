# Circuit Breaker Pattern in Spring Boot with Resilience4j

This project demonstrates the implementation of the Circuit Breaker pattern using Spring Boot and Resilience4j. It includes a mock external service that randomly fails and a protected service that uses a circuit breaker to gracefully handle those failures.

## Prerequisites

- Java 17
- Maven

## Build Instructions

To compile the project and download all required dependencies, run the following command from the root directory:

```bash
mvn clean install
```

## Run Instructions

To run the application locally, use the following command:

```bash
mvn spring-boot:run
```

Alternatively, you can run the generated JAR file:

```bash
java -jar target/circuit-breaker-0.0.1-SNAPSHOT.jar
```

The application will start on port `8080`.

## Testing Instructions

This project includes a shell script to demonstrate the circuit breaker functionality. The script uses `curl` to make requests to the API and trigger state transitions.

1. Ensure the Spring Boot application is running.
2. Open a new terminal window.
3. Make the test script executable (if you are on Linux/Mac/Git Bash):
   ```bash
   chmod +x test-circuit-breaker.sh
   ```
4. Run the script:
   ```bash
   ./test-circuit-breaker.sh
   ```

### What to expect during the test:
- **Initial State:** The circuit breaker starts in the `CLOSED` state.
- **Failures:** The script makes multiple requests to the API. Because the mock external API is designed to fail 70% of the time, the circuit breaker's failure rate threshold (50%) will be exceeded after the minimum number of calls (5).
- **Open State:** The circuit breaker transitions to the `OPEN` state. Subsequent requests will immediately return the fallback response without calling the external service.
- **Waiting:** The script waits for 10 seconds (`wait-duration-in-open-state`).
- **Half-Open State:** After the wait, the next request is allowed through. The circuit breaker enters the `HALF_OPEN` state.
- **Closing:** If the requests in the half-open state succeed, the circuit breaker returns to the `CLOSED` state.

You can monitor the application console output to see the state transition log messages.

## Endpoints

- `GET /api/users/{id}`: Fetches user data. Protected by the circuit breaker.
- `GET /api/circuit-breaker/state`: Returns the current state of the circuit breaker (`CLOSED`, `OPEN`, `HALF_OPEN`).
- `GET /api/circuit-breaker/metrics`: Returns simplified metrics for the circuit breaker.
- `GET /actuator/circuitbreakers`: Spring Boot Actuator endpoint for detailed circuit breaker metrics.
