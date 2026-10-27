## What
Employee hours tracker (Kotlin + Ktor, in-memory).

- `PUT /checkpoints`: store check-in/check-out events (idempotent by `id`, all-or-nothing validation)
- `GET /hours?employeeId&start&end`: pair check-ins with check-outs, clip to the range, sum minutes

## Run
```bash
./gradlew run      # http://localhost:8080
./gradlew test
```

## Try it
```bash
curl -X PUT localhost:8080/checkpoints -H "Content-Type: application/json" -d '[
  {"id":"c1","employeeId":"jc","type":"checkin","createdAt":"2026-10-27T09:00:00Z"},
  {"id":"c2","employeeId":"jc","type":"checkout","createdAt":"2026-10-27T12:00:00Z"}
]'

curl "localhost:8080/hours?employeeId=jc&start=2026-10-27T00:00:00Z&end=2026-10-28T00:00:00Z"
# → totalMinutes 180, totalHours 3.0
```

## Known issues
- A check-out with no prior check-in makes `GET /hours` return 400 for that employee.
- An invalid date returns 500 instead of 400.
