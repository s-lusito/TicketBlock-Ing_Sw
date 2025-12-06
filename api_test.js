/**
 * API Test Script for TicketBlock Application
 * 
 * This script tests the TicketBlock APIs by:
 * 1. Creating 2 organizers
 * 2. Creating 3 valid events (2 from organizer 1, 1 from organizer 2)
 * 3. Testing event creation edge cases
 * 4. Creating 3 users
 * 5. Testing ticket purchases with various scenarios
 * 6. Testing purchase limits and edge cases
 */

const BASE_URL = 'http://localhost:8080/api/v1';

// Test card numbers for validation testing
const TEST_CARDS = {
    VISA: '4532015112830366',
    MASTERCARD: '5425233430109903',
    INVALID: '1234567890123456'
};

// Test constants
const MAX_EVENT_NAME_LENGTH = 50;
const NON_EXISTENT_ID = 99999;
const EVENT_DATE_OFFSET_DAYS = {
    EVENT1: 180,  // ~6 months from now
    EVENT2: 220,  // ~7 months from now
    EVENT3: 250   // ~8 months from now
};

/**
 * Makes an HTTP request to the API
 * @param {string} endpoint - API endpoint path
 * @param {string} method - HTTP method (GET, POST, DELETE, etc.)
 * @param {object|null} body - Request body for POST/PUT requests
 * @param {string|null} token - JWT authentication token
 * @returns {Promise<{status: number, ok: boolean, data: object|null}>} Response object
 */
async function apiRequest(endpoint, method = 'GET', body = null, token = null) {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    const options = {
        method,
        headers
    };
    
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    try {
        const response = await fetch(`${BASE_URL}${endpoint}`, options);
        const contentType = response.headers.get('content-type');
        let data = null;
        
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        }
        
        return {
            status: response.status,
            ok: response.ok,
            data
        };
    } catch (error) {
        console.error(`❌ Request failed: ${error.message}`);
        throw error;
    }
}

// Wait function for delays
function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

// Test Results Tracking
const testResults = {
    passed: 0,
    failed: 0,
    total: 0
};

function logTest(testName, passed, message = '') {
    testResults.total++;
    if (passed) {
        testResults.passed++;
        console.log(`✅ ${testName}`);
    } else {
        testResults.failed++;
        console.log(`❌ ${testName}: ${message}`);
    }
}

// 1. Create Organizers
async function createOrganizers() {
    console.log('\n=== Creating 2 Organizers ===\n');
    
    const organizers = [];
    
    for (let i = 1; i <= 2; i++) {
        const organizerData = {
            firstName: `Organizer${i}`,
            lastName: `LastName${i}`,
            email: `organizer${i}@test.com`,
            password: `password123${i}`,
            role: 'ORGANIZER'
        };
        
        const response = await apiRequest('/auth/register', 'POST', organizerData);
        
        if (response.ok && response.data && response.data.token) {
            organizers.push({
                ...organizerData,
                token: response.data.token,
                userId: response.data.userId
            });
            logTest(`Create Organizer ${i}`, true);
            console.log(`   Token: ${response.data.token.substring(0, 20)}...`);
        } else {
            logTest(`Create Organizer ${i}`, false, `Status: ${response.status}`);
        }
    }
    
    return organizers;
}

// 2. Create Events
async function createEvents(organizers) {
    console.log('\n=== Creating 3 Valid Events ===\n');
    
    const events = [];
    
    // Get venue information first
    const venueResponse = await apiRequest('/venues/1', 'GET');
    if (!venueResponse.ok) {
        console.log('⚠️  Warning: Could not fetch venue information');
    }
    
    // Generate future dates dynamically
    const today = new Date();
    const futureDate1 = new Date(today);
    futureDate1.setDate(today.getDate() + EVENT_DATE_OFFSET_DAYS.EVENT1);
    const futureDate2 = new Date(today);
    futureDate2.setDate(today.getDate() + EVENT_DATE_OFFSET_DAYS.EVENT2);
    const futureDate3 = new Date(today);
    futureDate3.setDate(today.getDate() + EVENT_DATE_OFFSET_DAYS.EVENT3);
    
    const saleStartDate = new Date(today); // Today - so tickets are available for purchase immediately
    
    const formatDate = (date) => date.toISOString().split('T')[0];
    
    // Event 1 - Organizer 1
    const event1Data = {
        name: 'Concert Rock Festival 2026',
        date: formatDate(futureDate1),
        startTime: '20:00:00',
        endTime: '23:00:00',
        imageUrl: 'https://example.com/concert1.jpg',
        saleStartDate: formatDate(saleStartDate),
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
    };
    
    const event1Response = await apiRequest('/events', 'POST', event1Data, organizers[0].token);
    if (event1Response.ok && event1Response.data) {
        events.push(event1Response.data);
        logTest('Create Event 1 (Organizer 1)', true);
        console.log(`   Event ID: ${event1Response.data.id}, Name: ${event1Response.data.name}`);
    } else {
        logTest('Create Event 1 (Organizer 1)', false, `Status: ${event1Response.status}, ${JSON.stringify(event1Response.data)}`);
    }
    
    // Event 2 - Organizer 1
    const event2Data = {
        name: 'Jazz Night Special',
        date: formatDate(futureDate2),
        startTime: '19:30:00',
        endTime: '22:30:00',
        imageUrl: 'https://example.com/jazz.jpg',
        saleStartDate: formatDate(saleStartDate),
        venueId: 1,
        standardTicketPrice: 35.00,
        vipTicketPrice: 100.00
    };
    
    const event2Response = await apiRequest('/events', 'POST', event2Data, organizers[0].token);
    if (event2Response.ok && event2Response.data) {
        events.push(event2Response.data);
        logTest('Create Event 2 (Organizer 1)', true);
        console.log(`   Event ID: ${event2Response.data.id}, Name: ${event2Response.data.name}`);
    } else {
        logTest('Create Event 2 (Organizer 1)', false, `Status: ${event2Response.status}, ${JSON.stringify(event2Response.data)}`);
    }
    
    // Event 3 - Organizer 2
    const event3Data = {
        name: 'Comedy Show Extravaganza',
        date: formatDate(futureDate3),
        startTime: '21:00:00',
        endTime: '23:30:00',
        imageUrl: 'https://example.com/comedy.jpg',
        saleStartDate: formatDate(saleStartDate),
        venueId: 1,
        standardTicketPrice: 40.00,
        vipTicketPrice: 120.00
    };
    
    const event3Response = await apiRequest('/events', 'POST', event3Data, organizers[1].token);
    if (event3Response.ok && event3Response.data) {
        events.push(event3Response.data);
        logTest('Create Event 3 (Organizer 2)', true);
        console.log(`   Event ID: ${event3Response.data.id}, Name: ${event3Response.data.name}`);
    } else {
        logTest('Create Event 3 (Organizer 2)', false, `Status: ${event3Response.status}, ${JSON.stringify(event3Response.data)}`);
    }
    
    return events;
}

// 3. Test Event Creation Edge Cases
async function testEventEdgeCases(organizer) {
    console.log('\n=== Testing Event Creation Edge Cases ===\n');
    
    // Generate future dates dynamically
    const today = new Date();
    const futureDate = new Date(today);
    futureDate.setDate(today.getDate() + 180);
    const formatDate = (date) => date.toISOString().split('T')[0];
    const futureDateStr = formatDate(futureDate);
    
    const saleStart = new Date(today);
    saleStart.setDate(today.getDate() + 1);
    const saleStartStr = formatDate(saleStart);
    
    // Test 1: Invalid date (past date)
    const pastDateEvent = {
        name: 'Past Event',
        date: '2020-01-01',
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: '2019-12-01',
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
    };
    
    const pastDateResponse = await apiRequest('/events', 'POST', pastDateEvent, organizer.token);
    logTest('Event with past date should fail', !pastDateResponse.ok);
    
    // Test 2: Invalid time (end time before start time)
    const invalidTimeEvent = {
        name: 'Invalid Time Event',
        date: futureDateStr,
        startTime: '23:00:00',
        endTime: '20:00:00',
        saleStartDate: saleStartStr,
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
    };
    
    const invalidTimeResponse = await apiRequest('/events', 'POST', invalidTimeEvent, organizer.token);
    logTest('Event with end time before start time should fail', !invalidTimeResponse.ok);
    
    // Test 3: Negative ticket price
    const negativePrice = {
        name: 'Negative Price Event',
        date: futureDateStr,
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: saleStartStr,
        venueId: 1,
        standardTicketPrice: -10.00,
        vipTicketPrice: 150.00
    };
    
    const negativePriceResponse = await apiRequest('/events', 'POST', negativePrice, organizer.token);
    logTest('Event with negative price should fail', !negativePriceResponse.ok);
    
    // Test 4: Zero ticket price
    const zeroPrice = {
        name: 'Zero Price Event',
        date: futureDateStr,
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: saleStartStr,
        venueId: 1,
        standardTicketPrice: 0.00,
        vipTicketPrice: 150.00
    };
    
    const zeroPriceResponse = await apiRequest('/events', 'POST', zeroPrice, organizer.token);
    logTest('Event with zero price should fail', !zeroPriceResponse.ok);
    
    // Test 5: Missing required fields
    const missingFields = {
        name: 'Incomplete Event',
        date: futureDateStr
        // Missing other required fields
    };
    
    const missingFieldsResponse = await apiRequest('/events', 'POST', missingFields, organizer.token);
    logTest('Event with missing required fields should fail', !missingFieldsResponse.ok);
    
    // Test 6: Invalid venue ID
    const invalidVenue = {
        name: 'Invalid Venue Event',
        date: futureDateStr,
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: saleStartStr,
        venueId: 99999,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
    };
    
    const invalidVenueResponse = await apiRequest('/events', 'POST', invalidVenue, organizer.token);
    logTest('Event with non-existent venue should fail', !invalidVenueResponse.ok);
    
    // Test 7: Event name too long (over 50 characters)
    const longName = 'Event with very long name that exceeds maximum length limit'.substring(0, MAX_EVENT_NAME_LENGTH + 1);
    const longNameEvent = {
        name: longName, // 51 characters - exceeds limit
        date: futureDateStr,
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: saleStartStr,
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
    };
    
    const longNameResponse = await apiRequest('/events', 'POST', longNameEvent, organizer.token);
    logTest('Event with name over 50 characters should fail', !longNameResponse.ok);
    
    // Test 8: Sale start date after event date
    const pastSaleDate = new Date(today);
    pastSaleDate.setDate(today.getDate() - 10);
    const eventBeforeSale = new Date(today);
    eventBeforeSale.setDate(today.getDate() + 5);
    
    const invalidSaleDate = {
        name: 'Invalid Sale Date Event',
        date: formatDate(eventBeforeSale),
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: formatDate(futureDate), // Sale starts after event
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
    };
    
    const invalidSaleDateResponse = await apiRequest('/events', 'POST', invalidSaleDate, organizer.token);
    logTest('Event with sale start date after event date should fail', !invalidSaleDateResponse.ok);
}

// 4. Create Users
async function createUsers() {
    console.log('\n=== Creating 3 Users ===\n');
    
    const users = [];
    
    for (let i = 1; i <= 3; i++) {
        const userData = {
            firstName: `User${i}`,
            lastName: `TestUser${i}`,
            email: `user${i}@test.com`,
            password: `userpass123${i}`,
            role: 'USER'
        };
        
        const response = await apiRequest('/auth/register', 'POST', userData);
        
        if (response.ok && response.data && response.data.token) {
            users.push({
                ...userData,
                token: response.data.token,
                userId: response.data.userId
            });
            logTest(`Create User ${i}`, true);
            console.log(`   Token: ${response.data.token.substring(0, 20)}...`);
        } else {
            logTest(`Create User ${i}`, false, `Status: ${response.status}`);
        }
    }
    
    return users;
}

// 5. Get Available Tickets for an Event
async function getAvailableTickets(eventId, token) {
    const response = await apiRequest(`/events/${eventId}/tickets?ticketStatus=AVAILABLE`, 'GET', null, token);
    if (response.ok && response.data) {
        return response.data;
    }
    return [];
}

// 6. Test Ticket Purchase Scenarios
async function testTicketPurchases(user, events) {
    console.log('\n=== Testing Ticket Purchases (User 1) ===\n');
    
    if (events.length === 0) {
        console.log('⚠️  No events available to purchase tickets');
        return;
    }
    
    const eventId = events[0].id;
    
    // Get available tickets
    const availableTickets = await getAvailableTickets(eventId, user.token);
    
    if (availableTickets.length === 0) {
        console.log('⚠️  No available tickets for testing');
        return;
    }
    
    console.log(`   Found ${availableTickets.length} available tickets for event ${eventId}`);
    
    // Test 1: Purchase 1 ticket with fee
    if (availableTickets.length >= 1) {
        const ticketWithFee = {
            ticketFeeMap: {
                [availableTickets[0].id]: true // true means with fee
            },
            creditCardNumber: TEST_CARDS.VISA,
            expirationDate: '12/25',
            cvv: '123',
            cardHolderName: 'User One'
        };
        
        const withFeeResponse = await apiRequest('/tickets/purchase', 'POST', ticketWithFee, user.token);
        if (withFeeResponse.ok) {
            logTest('Purchase 1 ticket WITH fee', true);
            console.log(`   Total paid: ${withFeeResponse.data?.totalPrice || 'N/A'}`);
        } else {
            logTest('Purchase 1 ticket WITH fee', false, `Status: ${withFeeResponse.status}, ${JSON.stringify(withFeeResponse.data)}`);
        }
        
        await wait(500); // Small delay between requests
    }
    
    // Test 2: Purchase 1 ticket without fee
    if (availableTickets.length >= 2) {
        const ticketWithoutFee = {
            ticketFeeMap: {
                [availableTickets[1].id]: false // false means without fee
            },
            creditCardNumber: TEST_CARDS.MASTERCARD,
            expirationDate: '11/26',
            cvv: '456',
            cardHolderName: 'User One'
        };
        
        const withoutFeeResponse = await apiRequest('/tickets/purchase', 'POST', ticketWithoutFee, user.token);
        if (withoutFeeResponse.ok) {
            logTest('Purchase 1 ticket WITHOUT fee', true);
            console.log(`   Total paid: ${withoutFeeResponse.data?.totalPrice || 'N/A'}`);
        } else {
            logTest('Purchase 1 ticket WITHOUT fee', false, `Status: ${withoutFeeResponse.status}, ${JSON.stringify(withoutFeeResponse.data)}`);
        }
        
        await wait(500);
    }
    
    // Test 3: Purchase 2 tickets at once
    if (availableTickets.length >= 4) {
        const twoTickets = {
            ticketFeeMap: {
                [availableTickets[2].id]: true,  // One with fee
                [availableTickets[3].id]: false  // One without fee
            },
            creditCardNumber: TEST_CARDS.VISA,
            expirationDate: '12/25',
            cvv: '789',
            cardHolderName: 'User One'
        };
        
        const twoTicketsResponse = await apiRequest('/tickets/purchase', 'POST', twoTickets, user.token);
        if (twoTicketsResponse.ok) {
            logTest('Purchase 2 tickets at once', true);
            console.log(`   Total paid: ${twoTicketsResponse.data?.totalPrice || 'N/A'}`);
        } else {
            logTest('Purchase 2 tickets at once', false, `Status: ${twoTicketsResponse.status}, ${JSON.stringify(twoTicketsResponse.data)}`);
        }
    }
}

// 7. Test Purchase Edge Cases and Limits
async function testPurchaseLimits(user, events) {
    console.log('\n=== Testing Purchase Limits and Edge Cases ===\n');
    
    if (events.length === 0) {
        console.log('⚠️  No events available for testing');
        return;
    }
    
    const eventId = events[0].id;
    const availableTickets = await getAvailableTickets(eventId, user.token);
    
    // Test 1: Invalid credit card number
    if (availableTickets.length >= 1) {
        const invalidCard = {
            ticketFeeMap: {
                [availableTickets[0].id]: true
            },
            creditCardNumber: TEST_CARDS.INVALID,
            expirationDate: '12/25',
            cvv: '123',
            cardHolderName: 'Test User'
        };
        
        const invalidCardResponse = await apiRequest('/tickets/purchase', 'POST', invalidCard, user.token);
        logTest('Purchase with invalid credit card should fail', !invalidCardResponse.ok);
        
        await wait(500);
    }
    
    // Test 2: Invalid expiration date format
    if (availableTickets.length >= 1) {
        const invalidExpiry = {
            ticketFeeMap: {
                [availableTickets[0].id]: true
            },
            creditCardNumber: TEST_CARDS.VISA,
            expirationDate: '13/25', // Invalid month
            cvv: '123',
            cardHolderName: 'Test User'
        };
        
        const invalidExpiryResponse = await apiRequest('/tickets/purchase', 'POST', invalidExpiry, user.token);
        logTest('Purchase with invalid expiration date should fail', !invalidExpiryResponse.ok);
        
        await wait(500);
    }
    
    // Test 3: Invalid CVV (too short)
    if (availableTickets.length >= 1) {
        const invalidCVV = {
            ticketFeeMap: {
                [availableTickets[0].id]: true
            },
            creditCardNumber: TEST_CARDS.VISA,
            expirationDate: '12/25',
            cvv: '12', // Too short
            cardHolderName: 'Test User'
        };
        
        const invalidCVVResponse = await apiRequest('/tickets/purchase', 'POST', invalidCVV, user.token);
        logTest('Purchase with invalid CVV should fail', !invalidCVVResponse.ok);
        
        await wait(500);
    }
    
    // Test 4: Empty ticket map
    const emptyMap = {
        ticketFeeMap: {},
        creditCardNumber: TEST_CARDS.VISA,
        expirationDate: '12/25',
        cvv: '123',
        cardHolderName: 'Test User'
    };
    
    const emptyMapResponse = await apiRequest('/tickets/purchase', 'POST', emptyMap, user.token);
    logTest('Purchase with empty ticket map should fail', !emptyMapResponse.ok);
    
    // Test 5: Non-existent ticket ID
    const nonExistentTicket = {
        ticketFeeMap: {
            [NON_EXISTENT_ID]: true
        },
        creditCardNumber: TEST_CARDS.VISA,
        expirationDate: '12/25',
        cvv: '123',
        cardHolderName: 'Test User'
    };
    
    const nonExistentResponse = await apiRequest('/tickets/purchase', 'POST', nonExistentTicket, user.token);
    logTest('Purchase with non-existent ticket ID should fail', !nonExistentResponse.ok);
    
    // Test 6: Attempt to purchase already purchased ticket
    const myTickets = await apiRequest('/tickets/mine', 'GET', null, user.token);
    if (myTickets.ok && myTickets.data && myTickets.data.length > 0) {
        const alreadyPurchased = {
            ticketFeeMap: {
                [myTickets.data[0].id]: true
            },
            creditCardNumber: TEST_CARDS.VISA,
            expirationDate: '12/25',
            cvv: '123',
            cardHolderName: 'Test User'
        };
        
        const alreadyPurchasedResponse = await apiRequest('/tickets/purchase', 'POST', alreadyPurchased, user.token);
        logTest('Purchase already owned ticket should fail', !alreadyPurchasedResponse.ok);
    }
    
    // Test 7: Purchase without authentication
    if (availableTickets.length >= 1) {
        const noAuthPurchase = {
            ticketFeeMap: {
                [availableTickets[0].id]: true
            },
            creditCardNumber: TEST_CARDS.VISA,
            expirationDate: '12/25',
            cvv: '123',
            cardHolderName: 'Test User'
        };
        
        const noAuthResponse = await apiRequest('/tickets/purchase', 'POST', noAuthPurchase); // No token
        logTest('Purchase without authentication should fail', !noAuthResponse.ok);
    }
}

// 8. Additional Tests
async function additionalTests(users, organizers, events) {
    console.log('\n=== Additional Tests ===\n');
    
    // Generate future dates dynamically
    const today = new Date();
    const futureDate = new Date(today);
    futureDate.setDate(today.getDate() + 180);
    const formatDate = (date) => date.toISOString().split('T')[0];
    const saleStart = new Date(today);
    saleStart.setDate(today.getDate() + 1);
    
    // Test: User cannot create events
    if (users.length > 0 && users[0]) {
        const userEventAttempt = {
            name: 'User Created Event',
            date: formatDate(futureDate),
            startTime: '20:00:00',
            endTime: '23:00:00',
            saleStartDate: formatDate(saleStart),
            venueId: 1,
            standardTicketPrice: 50.00,
            vipTicketPrice: 150.00
        };
        
        const userEventResponse = await apiRequest('/events', 'POST', userEventAttempt, users[0].token);
        logTest('Regular user cannot create events', !userEventResponse.ok);
    }
    
    // Test: Organizer cannot purchase tickets from their own event (if implemented)
    if (organizers.length > 0 && events.length > 0) {
        const organizerTickets = await getAvailableTickets(events[0].id, organizers[0].token);
        if (organizerTickets.length > 0) {
            const organizerPurchase = {
                ticketFeeMap: {
                    [organizerTickets[0].id]: true
                },
                creditCardNumber: TEST_CARDS.VISA,
                expirationDate: '12/25',
                cvv: '123',
                cardHolderName: 'Organizer One'
            };
            
            const orgPurchaseResponse = await apiRequest('/tickets/purchase', 'POST', organizerPurchase, organizers[0].token);
            // This might be allowed or not depending on business rules
            logTest('Organizer ticket purchase attempt', orgPurchaseResponse.ok, 
                    orgPurchaseResponse.ok ? 'Allowed' : 'Not allowed');
        }
    }
    
    // Test: Get all events
    const allEventsResponse = await apiRequest('/events', 'GET');
    logTest('Get all events without authentication', allEventsResponse.ok);
    if (allEventsResponse.ok && allEventsResponse.data) {
        console.log(`   Found ${allEventsResponse.data.length} total events`);
    }
    
    // Test: Get specific event by ID
    if (events.length > 0) {
        const eventDetailResponse = await apiRequest(`/events/${events[0].id}`, 'GET');
        logTest('Get event by ID', eventDetailResponse.ok);
    }
    
    // Test: Delete event by organizer
    if (organizers.length > 0 && events.length > 1) {
        const deleteResponse = await apiRequest(`/events/${events[1].id}`, 'DELETE', null, organizers[0].token);
        logTest('Delete event by organizer', deleteResponse.ok || deleteResponse.status === 204);
    }
    
    // Test: User cannot delete events
    if (users.length > 0 && events.length > 0) {
        const userDeleteResponse = await apiRequest(`/events/${events[0].id}`, 'DELETE', null, users[0].token);
        logTest('Regular user cannot delete events', !userDeleteResponse.ok);
    }
}

// Main Test Runner
async function runTests() {
    console.log('╔════════════════════════════════════════════════════════════════╗');
    console.log('║         TicketBlock API Test Suite                             ║');
    console.log('╚════════════════════════════════════════════════════════════════╝\n');
    console.log(`Testing against: ${BASE_URL}`);
    console.log(`Start time: ${new Date().toISOString()}\n`);
    
    try {
        // Step 1: Create Organizers
        const organizers = await createOrganizers();
        
        if (organizers.length < 2) {
            console.log('\n⚠️  Failed to create organizers. Cannot continue with event tests.');
            return;
        }
        
        // Step 2: Create Events
        const events = await createEvents(organizers);
        
        // Step 3: Test Event Edge Cases
        await testEventEdgeCases(organizers[0]);
        
        // Step 4: Create Users
        const users = await createUsers();
        
        if (users.length < 1) {
            console.log('\n⚠️  Failed to create users. Cannot continue with purchase tests.');
            return;
        }
        
        // Step 5: Test Ticket Purchases
        await testTicketPurchases(users[0], events);
        
        // Step 6: Test Purchase Limits
        await testPurchaseLimits(users[1] || users[0], events);
        
        // Step 7: Additional Tests
        await additionalTests(users, organizers, events);
        
        // Print Summary
        console.log('\n╔════════════════════════════════════════════════════════════════╗');
        console.log('║                      Test Summary                              ║');
        console.log('╚════════════════════════════════════════════════════════════════╝\n');
        console.log(`Total Tests: ${testResults.total}`);
        console.log(`✅ Passed: ${testResults.passed}`);
        console.log(`❌ Failed: ${testResults.failed}`);
        console.log(`Success Rate: ${((testResults.passed / testResults.total) * 100).toFixed(2)}%`);
        console.log(`\nEnd time: ${new Date().toISOString()}`);
        
    } catch (error) {
        console.error('\n❌ Critical error during test execution:', error);
        process.exit(1);
    }
}

// Run the tests
runTests().catch(error => {
    console.error('Fatal error:', error);
    process.exit(1);
});
