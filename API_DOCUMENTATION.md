# TicketBlock API Documentation

This document provides comprehensive information for frontend developers on how to use the TicketBlock REST APIs.

## Table of Contents

1. [Authentication](#authentication)
2. [Events](#events)
3. [Tickets](#tickets)
4. [Venues](#venues)
5. [Error Handling](#error-handling)
6. [Common Data Types](#common-data-types)

---

## Base URL

All API endpoints are prefixed with:
```
http://localhost:8080/api/v1
```

---

## Authentication

### Register a New User

**Endpoint:** `POST /auth/register`

**Description:** Creates a new user account and returns a JWT token for authentication.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

**Field Constraints:**
- `email`: Valid email format, max 50 characters, must be unique
- `password`: Required, min 8 characters recommended
- `firstName`: Required, max 50 characters
- `lastName`: Required, max 50 characters
- `role`: Must be either `USER` or `ORGANIZER` (cannot register as `ADMIN`)

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Possible Errors:**
- `400 Bad Request`: Invalid role or validation failure
- `409 Conflict`: Email already registered

---

### Authenticate (Login)

**Endpoint:** `POST /auth/authenticate`

**Description:** Authenticates a user with email and password, returns a JWT token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Possible Errors:**
- `401 Unauthorized`: Invalid email or password
- `404 Not Found`: User not found

---

### Using JWT Tokens

Include the JWT token in all authenticated requests using the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Token Expiration:** Tokens expire after 30 days.

**Logout:** To logout, simply remove the token from the client (no server-side endpoint needed).

---

## Events

### Get All Events

**Endpoint:** `GET /events`

**Description:** Retrieves all events, optionally filtered by sale status.

**Query Parameters:**
- `saleStatus` (optional): Filter by status. Can specify multiple values.
  - Possible values: `NOT_STARTED`, `ONGOING`, `SOLD_OUT`, `ENDED`

**Example Requests:**
```
GET /events
GET /events?saleStatus=ONGOING
GET /events?saleStatus=ONGOING&saleStatus=NOT_STARTED
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Summer Music Festival",
    "organizer": {
      "id": 5,
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "jane@example.com",
      "role": "ORGANIZER"
    },
    "date": "2024-07-15",
    "startTime": "18:00:00",
    "endTime": "23:00:00",
    "eventSaleStatus": "ONGOING",
    "saleStartDate": "2024-07-01",
    "imageUrl": "https://example.com/image.jpg",
    "venue": {
      "id": 1,
      "name": "Central Stadium",
      "address": {
        "id": 1,
        "street": "123 Main St",
        "city": "New York",
        "state": "NY"
      }
    },
    "standardTicketPrice": 50.00,
    "vipTicketPrice": 150.00
  }
]
```

**Authorization:** Not required (public endpoint)

---

### Get Event by ID

**Endpoint:** `GET /events/{id}`

**Description:** Retrieves detailed information about a specific event.

**Path Parameters:**
- `id`: Event ID (integer)

**Example Request:**
```
GET /events/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Summer Music Festival",
  "organizer": {
    "id": 5,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane@example.com",
    "role": "ORGANIZER"
  },
  "date": "2024-07-15",
  "startTime": "18:00:00",
  "endTime": "23:00:00",
  "eventSaleStatus": "ONGOING",
  "saleStartDate": "2024-07-01",
  "imageUrl": "https://example.com/image.jpg",
  "venue": {
    "id": 1,
    "name": "Central Stadium",
    "address": {
      "id": 1,
      "street": "123 Main St",
      "city": "New York",
      "state": "NY"
    }
  },
  "standardTicketPrice": 50.00,
  "vipTicketPrice": 150.00
}
```

**Possible Errors:**
- `404 Not Found`: Event does not exist

**Authorization:** Not required (public endpoint)

---

### Create Event

**Endpoint:** `POST /events`

**Description:** Creates a new event. Automatically generates tickets for all seats in the selected venue.

**Required Role:** `ORGANIZER` or `ADMIN`

**Request Body:**
```json
{
  "name": "Summer Music Festival",
  "date": "2024-07-15",
  "startTime": "18:00:00",
  "endTime": "23:00:00",
  "saleStartDate": "2024-07-01",
  "imageUrl": "https://example.com/image.jpg",
  "venueId": 1,
  "standardTicketPrice": 50.00,
  "vipTicketPrice": 150.00
}
```

**Field Constraints:**
- `name`: Required, max 50 characters
- `date`: Required, must be in the future
- `startTime`: Required
- `endTime`: Required, must be after `startTime`
- `saleStartDate`: Required, must be at least 3 days before event date
- `venueId`: Required, must exist
- `standardTicketPrice`: Required, must be greater than zero
- `vipTicketPrice`: Required, must be greater than zero

**Response (200 OK):**
```json
{
  "id": 2,
  "name": "Summer Music Festival",
  ...
}
```

**Possible Errors:**
- `400 Bad Request`: Invalid dates or times, validation failure
- `403 Forbidden`: User is not an organizer
- `404 Not Found`: Venue not found
- `409 Conflict`: Venue not available for selected date/time

**Authorization:** Required (Bearer token)

---

### Delete Event

**Endpoint:** `DELETE /events/{id}`

**Description:** Deletes an event. Only the event organizer can delete their own events.

**Required Role:** `ORGANIZER` or `ADMIN`

**Path Parameters:**
- `id`: Event ID (integer)

**Example Request:**
```
DELETE /events/1
```

**Response (204 No Content)**

**Possible Errors:**
- `403 Forbidden`: User is not the event organizer
- `404 Not Found`: Event does not exist

**Authorization:** Required (Bearer token)

---

### Get Event Tickets

**Endpoint:** `GET /events/{id}/tickets`

**Description:** Retrieves all tickets for a specific event, optionally filtered by status.

**Path Parameters:**
- `id`: Event ID (integer)

**Query Parameters:**
- `ticketStatus` (optional): Filter by ticket status
  - Possible values: `AVAILABLE`, `SOLD`, `INVALIDATED`

**Example Requests:**
```
GET /events/1/tickets
GET /events/1/tickets?ticketStatus=AVAILABLE
```

**Response (200 OK):**
```json
[
  {
    "id": 101,
    "seat": {
      "id": 1,
      "seatNumber": 1
    },
    "owner": null,
    "price": 50.00,
    "resellable": false,
    "ticketStatus": "AVAILABLE"
  },
  {
    "id": 102,
    "seat": {
      "id": 2,
      "seatNumber": 2
    },
    "owner": {
      "id": 3,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com",
      "role": "USER"
    },
    "price": 50.00,
    "resellable": true,
    "ticketStatus": "SOLD"
  }
]
```

**Authorization:** Not required (public endpoint)

---

## Tickets

### Purchase Tickets

**Endpoint:** `POST /tickets/purchase`

**Description:** Purchases one or more tickets for an event. Maximum 4 tickets per user per event.

**Required Role:** `USER` or `ADMIN`

**Request Body:**
```json
{
  "ticketFeeMap": {
    "101": true,
    "102": false,
    "103": true
  },
  "creditCardNumber": "4532015112830366",
  "expirationDate": "12/25",
  "cvv": "123",
  "cardHolderName": "John Doe"
}
```

**Field Explanation:**
- `ticketFeeMap`: Map of ticket IDs to boolean values
  - `true`: Pay resale fee (10% extra) to make ticket resellable
  - `false`: No resale fee, ticket cannot be resold
- `creditCardNumber`: Valid credit card number (12-19 digits)
- `expirationDate`: MM/YY format
- `cvv`: 3-4 digit security code
- `cardHolderName`: 2-50 characters

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Purchase successful! Total amount charged: 110.00"
}
```

**Possible Errors:**
- `400 Bad Request`: Invalid payment information
- `403 Forbidden`: User already owns 4 tickets for this event
- `404 Not Found`: One or more tickets not found
- `409 Conflict`: Tickets not available, event sales closed, or payment failed

**Authorization:** Required (Bearer token)

---

### Resell Ticket

**Endpoint:** `POST /tickets/{id}/resell`

**Description:** Resells a ticket back to the marketplace. Only tickets purchased with the resale fee can be resold.

**Required Role:** `USER` or `ADMIN`

**Path Parameters:**
- `id`: Ticket ID (integer)

**Example Request:**
```
POST /tickets/102/resell
```

**Response (200 OK)**

**Possible Errors:**
- `403 Forbidden`: User doesn't own the ticket
- `404 Not Found`: Ticket not found
- `409 Conflict`: Ticket is not resellable (no resale fee was paid)

**Authorization:** Required (Bearer token)

---

### Get My Tickets

**Endpoint:** `GET /tickets/mine`

**Description:** Retrieves all tickets owned by the currently authenticated user.

**Required Role:** `USER` or `ADMIN`

**Example Request:**
```
GET /tickets/mine
```

**Response (200 OK):**
```json
[
  {
    "id": 102,
    "seat": {
      "id": 2,
      "seatNumber": 2
    },
    "owner": {
      "id": 3,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com",
      "role": "USER"
    },
    "price": 50.00,
    "resellable": true,
    "ticketStatus": "SOLD"
  }
]
```

**Authorization:** Required (Bearer token)

---

## Venues

### Get Venue by ID

**Endpoint:** `GET /venues/{id}`

**Description:** Retrieves detailed information about a venue, including all rows and seats.

**Path Parameters:**
- `id`: Venue ID (integer)

**Example Request:**
```
GET /venues/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Central Stadium",
  "address": {
    "id": 1,
    "street": "123 Main St",
    "city": "New York",
    "state": "NY"
  },
  "rows": [
    {
      "id": 1,
      "letter": "A",
      "sector": "STANDARD",
      "seats": [
        {
          "id": 1,
          "seatNumber": 1
        },
        {
          "id": 2,
          "seatNumber": 2
        }
      ]
    },
    {
      "id": 2,
      "letter": "B",
      "sector": "VIP",
      "seats": [
        {
          "id": 10,
          "seatNumber": 1
        }
      ]
    }
  ]
}
```

**Possible Errors:**
- `404 Not Found`: Venue does not exist

**Authorization:** Not required (public endpoint)

---

## Error Handling

All errors follow a consistent format:

### Error Response Structure

```json
{
  "status": 400,
  "detail": "Detailed error message for developers",
  "userMessage": "User-friendly error message",
  "errors": [
    {
      "field": "email",
      "message": "must be a well-formed email address"
    }
  ]
}
```

### Common HTTP Status Codes

- `200 OK`: Request succeeded
- `204 No Content`: Request succeeded with no response body
- `400 Bad Request`: Invalid request data or validation failure
- `401 Unauthorized`: Missing or invalid authentication token
- `403 Forbidden`: User doesn't have permission for this action
- `404 Not Found`: Resource not found
- `409 Conflict`: Business logic conflict (e.g., ticket unavailable, venue booked)
- `500 Internal Server Error`: Unexpected server error

---

## Common Data Types

### Event Sale Status

- `NOT_STARTED`: Sales have not begun yet
- `ONGOING`: Tickets are currently available for purchase
- `SOLD_OUT`: All tickets have been sold
- `ENDED`: Sales have ended (event is within 24 hours or has passed)

### Ticket Status

- `AVAILABLE`: Ticket is available for purchase
- `SOLD`: Ticket has been purchased
- `INVALIDATED`: Ticket has been invalidated and cannot be used

### User Roles

- `USER`: Regular user who can purchase tickets
- `ORGANIZER`: Can create and manage events
- `ADMIN`: System administrator (cannot be registered via API)

### Row Sectors

- `STANDARD`: Regular seating area with standard pricing
- `VIP`: Premium seating area with higher pricing

---

## Example Workflows

### Complete User Registration and Ticket Purchase Flow

1. **Register a new user:**
   ```
   POST /auth/register
   {
     "email": "john@example.com",
     "password": "securePass123",
     "firstName": "John",
     "lastName": "Doe",
     "role": "USER"
   }
   ```
   Response: `{ "token": "..." }`

2. **Browse available events:**
   ```
   GET /events?saleStatus=ONGOING
   ```

3. **View event details and tickets:**
   ```
   GET /events/1
   GET /events/1/tickets?ticketStatus=AVAILABLE
   ```

4. **Purchase tickets (with Bearer token):**
   ```
   POST /tickets/purchase
   Authorization: Bearer <token>
   {
     "ticketFeeMap": {
       "101": true,
       "102": true
     },
     "creditCardNumber": "4532015112830366",
     "expirationDate": "12/25",
     "cvv": "123",
     "cardHolderName": "John Doe"
   }
   ```

5. **View owned tickets:**
   ```
   GET /tickets/mine
   Authorization: Bearer <token>
   ```

6. **Resell a ticket (if resellable):**
   ```
   POST /tickets/101/resell
   Authorization: Bearer <token>
   ```

### Event Organizer Flow

1. **Register as organizer:**
   ```
   POST /auth/register
   {
     "email": "organizer@example.com",
     "password": "securePass123",
     "firstName": "Jane",
     "lastName": "Smith",
     "role": "ORGANIZER"
   }
   ```

2. **View available venues:**
   ```
   GET /venues/1
   ```

3. **Create an event:**
   ```
   POST /events
   Authorization: Bearer <token>
   {
     "name": "Summer Concert",
     "date": "2024-08-01",
     "startTime": "19:00:00",
     "endTime": "23:00:00",
     "saleStartDate": "2024-07-20",
     "venueId": 1,
     "standardTicketPrice": 50.00,
     "vipTicketPrice": 150.00
   }
   ```

4. **Check ticket sales:**
   ```
   GET /events/2/tickets
   ```

5. **Delete event if needed:**
   ```
   DELETE /events/2
   Authorization: Bearer <token>
   ```

---

## Notes for Frontend Developers

1. **Always include the JWT token** in the Authorization header for authenticated endpoints.

2. **Token storage:** Store the JWT token securely (e.g., in httpOnly cookies or secure localStorage).

3. **Token refresh:** Tokens expire after 30 days. Implement token expiration handling and prompt users to re-authenticate.

4. **Validation:** The API performs server-side validation, but implement client-side validation for better UX.

5. **Error handling:** Always handle API errors gracefully and display user-friendly messages from the `userMessage` field.

6. **Date/Time formats:**
   - Dates: `YYYY-MM-DD` (ISO 8601)
   - Times: `HH:mm:ss` (24-hour format)
   - Expiration dates: `MM/YY`

7. **Decimal precision:** All prices use 2 decimal places (e.g., `50.00`).

8. **Ticket purchase limit:** Users cannot purchase more than 4 tickets per event in total (across multiple purchases).

9. **Resale fee:** The resale fee is 10% of the ticket price. Calculate and display this to users during purchase.

10. **Public vs Authenticated endpoints:**
    - Public: Event listings, event details, venue details
    - Authenticated: Ticket purchase, reselling, user tickets, event creation/deletion

---

For any questions or issues with the API, please contact the backend development team.
