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

## Test 1

### PUT http://localhost:8080/checkpoints
<img width="728" height="460" alt="Screenshot 2026-10-27 at 4 14 41 p m" src="https://github.com/user-attachments/assets/c5f0b833-7929-49ef-a6bd-69841776248a" />

### http://localhost:8080/hours?employeeId=jc&start=2026-10-28T10:00:00Z&end=2026-10-28T15:00:00Z
<img width="804" height="502" alt="Screenshot 2026-10-27 at 4 15 42 p m" src="https://github.com/user-attachments/assets/a31e2fb0-8bf2-4d46-9fbb-9419d1a4ad67" />

## Test 2: messy data, out of order
