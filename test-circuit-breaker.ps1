$BASE_URL = "http://localhost:8080/api"

Write-Host "=== Circuit Breaker Demo ==="
Write-Host "The mock API is configured to fail 70% of the time."
Write-Host "The Circuit Breaker requires 5 minimum calls and a 50% failure rate to open."

Write-Host "`n1. Initial State:"
(Invoke-WebRequest -Uri "$BASE_URL/circuit-breaker/state" -UseBasicParsing).Content

Write-Host "`n2. Triggering failures to open the circuit..."
for ($i = 1; $i -le 10; $i++) {
    Write-Host -NoNewline "Request ${i}: "
    try {
        $response = Invoke-RestMethod -Uri "$BASE_URL/users/$i"
        if ($response.id) {
            Write-Host "`"id`":`"$($response.id)`""
        } else {
            Write-Host "Failed"
        }
    } catch {
        Write-Host "Failed"
    }
    Start-Sleep -Milliseconds 500
}

Write-Host "`n3. Checking State (Should be OPEN):"
(Invoke-WebRequest -Uri "$BASE_URL/circuit-breaker/state" -UseBasicParsing).Content

Write-Host "`n4. Waiting for 10 seconds for the waitDurationInOpenState to pass..."
Start-Sleep -Seconds 10

Write-Host "`n5. Making a new request. This should succeed (if mock API succeeds) and move state to HALF_OPEN."
Write-Host -NoNewline "Request after wait: "
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/users/100"
    if ($response.id) {
        Write-Host "`"id`":`"$($response.id)`""
    } else {
        Write-Host "Failed"
    }
} catch {
    Write-Host "Failed"
}

Write-Host "`n6. Checking State (Should be HALF_OPEN or CLOSED):"
(Invoke-WebRequest -Uri "$BASE_URL/circuit-breaker/state" -UseBasicParsing).Content

Write-Host "`n7. Making a few more requests to stabilize to CLOSED..."
for ($i = 1; $i -le 5; $i++) {
    Write-Host -NoNewline "Request ${i}: "
    try {
        $response = Invoke-RestMethod -Uri "$BASE_URL/users/200$i"
        if ($response.id) {
            Write-Host "`"id`":`"$($response.id)`""
        } else {
            Write-Host "Failed"
        }
    } catch {
        Write-Host "Failed"
    }
    Start-Sleep -Milliseconds 500
}

Write-Host "`n8. Final State:"
(Invoke-WebRequest -Uri "$BASE_URL/circuit-breaker/state" -UseBasicParsing).Content
