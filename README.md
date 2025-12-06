# TicketBlock API Test Script

This script comprehensively tests the TicketBlock REST APIs.

## What the Script Tests

The `api_test.js` script performs the following operations:

### 1. **Creates 2 Organizers**
   - Registers two users with ORGANIZER role
   - Stores authentication tokens for subsequent API calls

### 2. **Creates 3 Valid Events**
   - Creates 2 events from Organizer 1:
     - "Concert Rock Festival 2025" (€50 standard, €150 VIP)
     - "Jazz Night Special" (€35 standard, €100 VIP)
   - Creates 1 event from Organizer 2:
     - "Comedy Show Extravaganza" (€40 standard, €120 VIP)

### 3. **Tests Event Creation Edge Cases**
   - ❌ Event with past date
   - ❌ Event with end time before start time
   - ❌ Event with negative ticket price
   - ❌ Event with zero ticket price
   - ❌ Event with missing required fields
   - ❌ Event with non-existent venue ID
   - ❌ Event with name over 50 characters
   - ❌ Event with sale start date after event date

### 4. **Creates 3 Users**
   - Registers three users with USER role
   - Stores authentication tokens for purchase testing

### 5. **Tests Ticket Purchases (User 1)**
   - ✅ Purchase 1 ticket **WITH fee**
   - ✅ Purchase 1 ticket **WITHOUT fee**
   - ✅ Purchase 2 tickets at once (one with fee, one without)

### 6. **Tests Purchase Limits and Edge Cases**
   - ❌ Purchase with invalid credit card number
   - ❌ Purchase with invalid expiration date (month 13)
   - ❌ Purchase with invalid CVV (too short)
   - ❌ Purchase with empty ticket map
   - ❌ Purchase with non-existent ticket ID
   - ❌ Purchase already owned ticket
   - ❌ Purchase without authentication

### 7. **Additional Tests**
   - User authorization tests
   - Event retrieval tests
   - Event deletion tests
   - Role-based access control tests

## Prerequisites

1. **PostgreSQL Database Running**
   ```bash
   docker-compose up -d
   ```

2. **Spring Boot Backend Running**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   
   Or if using Maven wrapper on Windows:
   ```bash
   cd backend
   mvnw.cmd spring-boot:run
   ```

3. **Node.js 18 or higher**
   - Check version: `node --version`
   - The script uses the native `fetch` API available in Node.js 18+

## Running the Tests

### Method 1: Using npm script (recommended)

```bash
npm test
```

or

```bash
npm run test:api
```

### Method 2: Direct execution

```bash
node api_test.js
```

## Expected Output

The script will output detailed test results with the following indicators:
- ✅ Green checkmarks for successful tests
- ❌ Red X marks for failed tests (expected for edge case testing)
- Test summary with pass/fail counts and success rate

Example output:
```
╔════════════════════════════════════════════════════════════════╗
║         TicketBlock API Test Suite                             ║
╚════════════════════════════════════════════════════════════════╝

Testing against: http://localhost:8080/api/v1
Start time: 2025-12-06T19:30:00.000Z

=== Creating 2 Organizers ===

✅ Create Organizer 1
   Token: eyJhbGciOiJIUzI1NiIs...
✅ Create Organizer 2
   Token: eyJhbGciOiJIUzI1NiIs...

=== Creating 3 Valid Events ===

✅ Create Event 1 (Organizer 1)
   Event ID: 1, Name: Concert Rock Festival 2025
✅ Create Event 2 (Organizer 1)
   Event ID: 2, Name: Jazz Night Special
✅ Create Event 3 (Organizer 2)
   Event ID: 3, Name: Comedy Show Extravaganza

...

╔════════════════════════════════════════════════════════════════╗
║                      Test Summary                              ║
╚════════════════════════════════════════════════════════════════╝

Total Tests: 35
✅ Passed: 35
❌ Failed: 0
Success Rate: 100.00%
```

## API Endpoints Tested

- `POST /api/v1/auth/register` - User/Organizer registration
- `POST /api/v1/events` - Event creation
- `GET /api/v1/events` - List all events
- `GET /api/v1/events/{id}` - Get event by ID
- `DELETE /api/v1/events/{id}` - Delete event
- `GET /api/v1/events/{id}/tickets` - Get event tickets
- `POST /api/v1/tickets/purchase` - Purchase tickets
- `GET /api/v1/tickets/mine` - Get user's tickets
- `GET /api/v1/venues/{id}` - Get venue information

## Configuration

The base URL for the API is configured at the top of `api_test.js`:

```javascript
const BASE_URL = 'http://localhost:8080/api/v1';
```

Modify this if your backend runs on a different host or port.

## Test Data

### Credit Card Numbers (Valid Test Cards)
- Visa: `4532015112830366`
- Mastercard: `5425233430109903`

### Users Created
- **Organizers**: organizer1@test.com, organizer2@test.com
- **Users**: user1@test.com, user2@test.com, user3@test.com
- **Password pattern**: password123{number} or userpass123{number}

### Events Created
All events use Venue ID 1 (Arena Grande Spettacoli) which is pre-loaded from the database initialization.

## Troubleshooting

### Error: "fetch is not defined"
- Ensure you're using Node.js 18 or higher
- Upgrade Node.js: `nvm install 18` or download from nodejs.org

### Error: "Connection refused"
- Ensure the Spring Boot backend is running on port 8080
- Check if PostgreSQL is running: `docker ps`

### Error: "Could not fetch venue information"
- Ensure the database has been initialized with venue data
- Check that the DataInitializer has loaded venue_data.json

### All tests failing with 401 Unauthorized
- Check that JWT authentication is properly configured in the backend
- Verify that the authentication endpoints are accessible

### Tests fail with validation errors
- Check that the request DTOs match the backend validation rules
- Review the backend logs for detailed error messages

## Notes

- The script uses the database initialization data (venue_data.json)
- Each test run creates new users, organizers, and events
- The database is set to `ddl-auto: create-drop`, so data is reset on backend restart
- Test results are cumulative - both successful and expected failures are counted

## License

ISC
