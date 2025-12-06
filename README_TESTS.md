# TicketBlock API Test Suite

This test suite validates the sold-out and reselling functionality of the TicketBlock API using the Mini Teatro Test venue.

## Prerequisites

- Node.js 18.0.0 or higher (uses native fetch API)
- Running TicketBlock backend server

## Setup

No additional dependencies required! The test uses Node.js built-in features only.

## Running the Tests

### Default (localhost)
```bash
node api_test.js
```

### Custom API URL
```bash
API_BASE_URL=http://your-server:port/api/v1 node api_test.js
```

### Using npm
```bash
npm test
```

## Test Scenarios

The `testSoldOutAndReselling()` function tests the following scenarios:

### 1. Event Creation on Mini Teatro Test
- Creates an event on venue ID 3 (Mini Teatro Test)
- Mini Teatro has exactly 4 seats in row A
- Event date: 200 days in the future
- Sale start date: Tomorrow

### 2. Sequential Ticket Purchases
- User 1 purchases 1 ticket
- User 2 purchases 1 ticket  
- User 3 purchases 2 tickets
- Total: 4 tickets (full capacity)

### 3. Sold-Out State Verification
- Confirms no tickets available
- Attempts purchase when sold out (should fail with 400/404)

### 4. 4-Ticket Maximum Limit per User per Event
- Tests that a user can purchase up to 4 tickets for a single event
- Verifies the limit is per event, not global

### 5. Ticket Reselling
- Attempts to resell a purchased ticket
- Verifies ticket availability after resale (if reselling is allowed)

## Test Data

### Credit Cards
All credit cards are Luhn algorithm compliant:
- VISA: `4532015112830366`
- MASTERCARD: `5425233430109903`
- AMEX: `374245455400126`

Card expiration dates are dynamically generated (2 years from test run).

### Mini Teatro Test Venue
- Venue ID: 3
- Seats: 4 (Row A, seats 1-4)
- Located in Test City, Testing

## Expected Results

When all tests pass, you should see:
```
✓ PASS - Create Mini Teatro Event
✓ PASS - Verify Initial Ticket Count: 4 tickets available
✓ PASS - User 1 Purchase (1 ticket)
✓ PASS - User 2 Purchase (1 ticket)
✓ PASS - User 3 Purchase (2 tickets)
✓ PASS - Verify Sold Out State: No tickets available
✓ PASS - Purchase Attempt When Sold Out: Correctly rejected
✓ PASS - 4-Ticket Maximum Limit
... and more
```

## Troubleshooting

### Backend not running
```
Error: ECONNREFUSED
```
**Solution**: Start the TicketBlock backend server first.

### Authentication failures
```
Status: 401
```
**Solution**: Check that the authentication endpoints are working correctly.

### Wrong number of tickets
```
✗ FAIL - Verify Initial Ticket Count: Expected 4 tickets, got X
```
**Solution**: Verify that Mini Teatro Test (venue ID 3) is configured with exactly 4 seats.

## API Endpoints Used

- `POST /auth/register` - User registration
- `POST /auth/authenticate` - User authentication (if separate from registration)
- `POST /events` - Create event
- `GET /events/{id}/tickets` - Get available tickets
- `POST /tickets/purchase` - Purchase tickets
- `POST /tickets/{id}/resell` - Resell ticket

## Exit Codes

- `0` - All tests passed
- `1` - One or more tests failed or error occurred
