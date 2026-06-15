#!/bin/bash

BASE_URL="http://localhost:8080/api"
MOCK_API_URL="http://localhost:8080/mock-api"

echo "=== Circuit Breaker Demo ==="
echo "The mock API is configured to fail 70% of the time."
echo "The Circuit Breaker requires 5 minimum calls and a 50% failure rate to open."

echo -e "\n1. Initial State:"
curl -s $BASE_URL/circuit-breaker/state
echo ""

echo -e "\n2. Triggering failures to open the circuit..."
for i in {1..10}
do
   echo -n "Request $i: "
   curl -s -w "\n" $BASE_URL/users/$i | grep -o '"id":"[^"]*"' || echo "Failed"
   sleep 0.5
done

echo -e "\n3. Checking State (Should be OPEN):"
curl -s $BASE_URL/circuit-breaker/state
echo ""

echo -e "\n4. Waiting for 10 seconds for the waitDurationInOpenState to pass..."
sleep 10

echo -e "\n5. Making a new request. This should succeed (if mock API succeeds) and move state to HALF_OPEN."
# Note: The mock API fails 70% of the time, so we might need a couple of tries to get a successful response and close the circuit.
echo -n "Request after wait: "
curl -s -w "\n" $BASE_URL/users/100 | grep -o '"id":"[^"]*"' || echo "Failed"

echo -e "\n6. Checking State (Should be HALF_OPEN or CLOSED):"
curl -s $BASE_URL/circuit-breaker/state
echo ""

echo -e "\n7. Making a few more requests to stabilize to CLOSED..."
for i in {1..5}
do
   echo -n "Request $i: "
   curl -s -w "\n" $BASE_URL/users/200$i | grep -o '"id":"[^"]*"' || echo "Failed"
   sleep 0.5
done

echo -e "\n8. Final State:"
curl -s $BASE_URL/circuit-breaker/state
echo ""
