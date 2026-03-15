
# Transaction Service
Basic service for storing and retrieving casino transactions.

## Setup Guide
To run the service:
* Ensure Java 21 or later is installed.
* Navigate to the project root dir.
* Start the application (two options):
  * Using bootRun:
      * Mac / Linux: `./gradlew bootRun`
      * Windows: `gradlew.bat bootRun`
  * Create a runnable jar:
    * Mac / Linux: `./gradlew bootJar`
    * Windows: `gradlew.bat bootJar`
    * `java -jar build/libs/transaction-service-1.0.jar`
* The API will be available at: http://localhost:8080/api/v1/transactions


## Notes

Due to the 4-hour time constraint, the main goal was to create a somewhat fleshed-out solution while compromising in the following larger areas:
* Retrieval Functionality: The API supports basic retrieval use-cases. In a production system this would likely require additional query patterns (based on business requirements) and full paging support. 
* Round / Game State Tracking: The services main concern is transaction-level tracking.  Round statuses and operator-specific data are not maintained. 
* Testing: Unit tests are minimal.  With additional time, a more robust test suite would be introduced.
* Storage: H2 and JPA were chosen just to make things fast and simple.  
* API Security: This is non-existent due to time constraints.
* Concurrency: Minimal concurrency safeguards are implemented using an optimistic lock.
* Observability: Logging is minimal and there are no metrics currently.
* Analytics: A production service would typically include an event integration to support downstream analytics. 
* Documentation: The code should be easy to follow, however, additional method/class-level documentation could be added.


## Testing
Below are example curl commands for interacting with the API:

* Create a wager

```
curl -X POST "http://localhost:8080/api/v1/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "17aebb5b-c15d-45c3-9a41-babc10fedbb2",
    "roundId": "13a5fbb8-69c6-4068-88ec-2a02b9fcef6d",
    "playerId": "cool-guy-123",
    "gameName": "cool-slot-game",
    "amount": 10.50,
    "type": "WAGER",
    "currency": "USD"
  }'
  ```
* Mark the wager completed:
```
curl -X PATCH "http://localhost:8080/api/v1/transactions/17aebb5b-c15d-45c3-9a41-babc10fedbb2/COMPLETED"
```
* Create a result:
```
curl -X POST "http://localhost:8080/api/v1/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "3415b398-2408-4680-a774-c04c9d886f56",
    "roundId": "13a5fbb8-69c6-4068-88ec-2a02b9fcef6d",
    "playerId": "cool-guy-123",
    "gameName": "cool-slot-game",
    "amount": 1000.50,
    "type": "RESULT",
    "currency": "USD"
  }'
```
* Cancellation:
```
curl -X PATCH "http://localhost:8080/api/v1/transactions/3415b398-2408-4680-a774-c04c9d886f56/CANCELLED"
```
* Fetch:
```
curl "http://localhost:8080/api/v1/transactions/17aebb5b-c15d-45c3-9a41-babc10fedbb2"

curl "http://localhost:8080/api/v1/transactions/round/13a5fbb8-69c6-4068-88ec-2a02b9fcef6d"

curl "http://localhost:8080/api/v1/transactions/player/cool-guy-123/range?start=2026-03-01T00:00:00Z&end=2026-03-31T23:59:59Z"
```

* Error example (missing wager):
```

curl -X POST "http://localhost:8080/api/v1/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "65e605a8-60d4-47a9-bba4-5a65cd256d8e",
    "roundId": "6094ee30-1dff-4f2c-b3f6-76517904f7b3",
    "playerId": "cool-guy-123",
    "gameName": "cool-slot-game",
    "amount": 1000.50,
    "type": "RESULT",
    "currency": "USD"
  }'
```
* Error Response:
```
{"errorCode":"INVALID_CREATE","message":"unable to create result transaction: 65e605a8-60d4-47a9-bba4-5a65cd256d8e, no successful wager found"}%`
```

