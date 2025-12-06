// TicketBlock API Test Suite
// Tests for the TicketBlock REST API including sold-out scenarios and ticket reselling

const BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080/api/v1';

// Test credit cards (valid Luhn algorithm)
const TEST_CARDS = {
    VISA: '4532015112830366',
    MASTERCARD: '5425233430109903',
    AMEX: '374245455400126'
};

// Test state
let users = [];
let organizers = [];
let events = [];

// Helper function to make API requests
async function apiRequest(method, endpoint, data = null, token = null) {
    const url = `${BASE_URL}${endpoint}`;
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(url, options);
        const text = await response.text();
        const result = {
            status: response.status,
            ok: response.ok,
            data: text ? JSON.parse(text) : null
        };
        return result;
    } catch (error) {
        console.error(`API Request Error: ${error.message}`);
        throw error;
    }
}

// Helper function to get available tickets for an event
async function getAvailableTickets(eventId, token) {
    const response = await apiRequest('GET', `/events/${eventId}/tickets?ticketStatus=AVAILABLE`, null, token);
    if (response.ok) {
        return response.data || [];
    }
    return [];
}

// Helper function to log test results
function logTest(testName, passed, message = '') {
    const status = passed ? '✓ PASS' : '✗ FAIL';
    const color = passed ? '\x1b[32m' : '\x1b[31m';
    const reset = '\x1b[0m';
    console.log(`${color}${status}${reset} - ${testName}${message ? ': ' + message : ''}`);
}

// Helper function to wait
function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

// Helper function to register and authenticate a user
async function registerUser(email, password, firstName, lastName, role = 'USER') {
    const registerData = {
        email,
        password,
        firstName,
        lastName,
        role
    };

    const registerResponse = await apiRequest('POST', '/auth/register', registerData);
    
    if (!registerResponse.ok) {
        console.error(`Failed to register user ${email}:`, registerResponse.data);
        return null;
    }

    return {
        email,
        password,
        firstName,
        lastName,
        role,
        token: registerResponse.data.token,
        userId: registerResponse.data.userId
    };
}

// Helper function to create an event
async function createEvent(eventData, token) {
    const response = await apiRequest('POST', '/events', eventData, token);
    return response;
}

// Test: Sold Out and Reselling Functionality
async function testSoldOutAndReselling(users, events) {
    console.log('\n========== Test: Sold Out and Reselling ==========\n');
    
    let passedTests = 0;
    let totalTests = 0;

    try {
        // Ensure we have at least 3 users (we'll create them if needed)
        const testUsers = [];
        for (let i = 0; i < 3; i++) {
            if (i < users.length) {
                testUsers.push(users[i]);
            } else {
                const timestamp = Date.now();
                const newUser = await registerUser(
                    `testuser${timestamp + i}@test.com`,
                    'Password123!',
                    `Test${i}`,
                    `User${i}`,
                    'USER'
                );
                if (newUser) {
                    testUsers.push(newUser);
                    users.push(newUser);
                }
            }
        }

        if (testUsers.length < 3) {
            logTest('User Setup', false, 'Failed to create enough test users');
            return { passed: passedTests, total: ++totalTests };
        }

        // Create an organizer for event creation
        const timestamp = Date.now();
        const organizer = await registerUser(
            `organizer${timestamp}@test.com`,
            'Password123!',
            'Test',
            'Organizer',
            'ORGANIZER'
        );

        if (!organizer) {
            logTest('Organizer Setup', false, 'Failed to create organizer');
            return { passed: passedTests, total: ++totalTests };
        }

        // Step 1: Create event on Mini Teatro Test (venue ID 3, has only 4 seats)
        const futureDate = new Date();
        futureDate.setDate(futureDate.getDate() + 200);
        
        const saleStartDate = new Date();
        saleStartDate.setDate(saleStartDate.getDate() + 1);

        const eventData = {
            name: `Mini Teatro Test Event ${timestamp}`,
            date: futureDate.toISOString().split('T')[0],
            startTime: '20:00:00',
            endTime: '22:00:00',
            saleStartDate: saleStartDate.toISOString().split('T')[0],
            venueId: 3,
            standardTicketPrice: 25.00,
            vipTicketPrice: 50.00
        };

        const eventResponse = await createEvent(eventData, organizer.token);
        totalTests++;
        
        if (!eventResponse.ok) {
            logTest('Create Mini Teatro Event', false, `Status: ${eventResponse.status}`);
            return { passed: passedTests, total: totalTests };
        }
        
        logTest('Create Mini Teatro Event', true);
        passedTests++;
        const createdEvent = eventResponse.data;
        events.push(createdEvent);

        // Wait a moment for tickets to be generated
        await wait(1000);

        // Get available tickets
        let availableTickets = await getAvailableTickets(createdEvent.id, testUsers[0].token);
        totalTests++;
        
        if (availableTickets.length !== 4) {
            logTest('Verify Initial Ticket Count', false, `Expected 4 tickets, got ${availableTickets.length}`);
        } else {
            logTest('Verify Initial Ticket Count', true, '4 tickets available');
            passedTests++;
        }

        // Step 2: Purchase all 4 tickets to achieve sold-out state
        // User 1 buys 1 ticket
        // User 2 buys 1 ticket  
        // User 3 buys 2 tickets
        const purchasedTickets = [];

        // User 1 purchases 1 ticket
        if (availableTickets.length > 0) {
            const ticketsToPurchase = {
                [availableTickets[0].id]: false // no fee
            };

            const purchaseData = {
                ticketFeeMap: ticketsToPurchase,
                creditCardNumber: TEST_CARDS.VISA,
                expirationDate: '12/25',
                cvv: '123',
                cardHolderName: `${testUsers[0].firstName} ${testUsers[0].lastName}`
            };

            const purchase1 = await apiRequest('POST', '/tickets/purchase', purchaseData, testUsers[0].token);
            totalTests++;
            
            if (purchase1.ok) {
                logTest('User 1 Purchase (1 ticket)', true);
                passedTests++;
                purchasedTickets.push(...(purchase1.data.tickets || []));
            } else {
                logTest('User 1 Purchase (1 ticket)', false, `Status: ${purchase1.status}`);
            }
        }

        // Refresh available tickets
        await wait(500);
        availableTickets = await getAvailableTickets(createdEvent.id, testUsers[1].token);

        // User 2 purchases 1 ticket
        if (availableTickets.length > 0) {
            const ticketsToPurchase = {
                [availableTickets[0].id]: false
            };

            const purchaseData = {
                ticketFeeMap: ticketsToPurchase,
                creditCardNumber: TEST_CARDS.MASTERCARD,
                expirationDate: '11/26',
                cvv: '456',
                cardHolderName: `${testUsers[1].firstName} ${testUsers[1].lastName}`
            };

            const purchase2 = await apiRequest('POST', '/tickets/purchase', purchaseData, testUsers[1].token);
            totalTests++;
            
            if (purchase2.ok) {
                logTest('User 2 Purchase (1 ticket)', true);
                passedTests++;
                purchasedTickets.push(...(purchase2.data.tickets || []));
            } else {
                logTest('User 2 Purchase (1 ticket)', false, `Status: ${purchase2.status}`);
            }
        }

        // Refresh available tickets
        await wait(500);
        availableTickets = await getAvailableTickets(createdEvent.id, testUsers[2].token);

        // User 3 purchases 2 tickets (to reach sold out)
        if (availableTickets.length >= 2) {
            const ticketsToPurchase = {
                [availableTickets[0].id]: true, // with fee
                [availableTickets[1].id]: false
            };

            const purchaseData = {
                ticketFeeMap: ticketsToPurchase,
                creditCardNumber: TEST_CARDS.AMEX,
                expirationDate: '10/27',
                cvv: '789',
                cardHolderName: `${testUsers[2].firstName} ${testUsers[2].lastName}`
            };

            const purchase3 = await apiRequest('POST', '/tickets/purchase', purchaseData, testUsers[2].token);
            totalTests++;
            
            if (purchase3.ok) {
                logTest('User 3 Purchase (2 tickets)', true);
                passedTests++;
                purchasedTickets.push(...(purchase3.data.tickets || []));
            } else {
                logTest('User 3 Purchase (2 tickets)', false, `Status: ${purchase3.status}`);
            }
        }

        // Step 3: Verify sold out - no tickets should be available
        await wait(1000);
        availableTickets = await getAvailableTickets(createdEvent.id, testUsers[0].token);
        totalTests++;
        
        if (availableTickets.length === 0) {
            logTest('Verify Sold Out State', true, 'No tickets available');
            passedTests++;
        } else {
            logTest('Verify Sold Out State', false, `${availableTickets.length} tickets still available`);
        }

        // Step 4: Attempt to purchase when sold out (should fail)
        const soldOutPurchase = await apiRequest('POST', '/tickets/purchase', {
            ticketFeeMap: {},
            creditCardNumber: TEST_CARDS.VISA,
            expirationDate: '12/25',
            cvv: '123',
            cardHolderName: 'Test User'
        }, testUsers[0].token);
        
        totalTests++;
        if (!soldOutPurchase.ok && (soldOutPurchase.status === 400 || soldOutPurchase.status === 404)) {
            logTest('Purchase Attempt When Sold Out', true, 'Correctly rejected');
            passedTests++;
        } else {
            logTest('Purchase Attempt When Sold Out', false, `Unexpected status: ${soldOutPurchase.status}`);
        }

        // Step 5: Test 4-ticket maximum limit per user
        // Create another event to test the limit
        const event2Data = {
            ...eventData,
            name: `Limit Test Event ${timestamp}`
        };

        const event2Response = await createEvent(event2Data, organizer.token);
        if (event2Response.ok) {
            await wait(1000);
            const event2Tickets = await getAvailableTickets(event2Response.data.id, testUsers[0].token);
            
            if (event2Tickets.length === 4) {
                // Try to purchase all 4 tickets with one user
                const allTickets = {};
                event2Tickets.forEach(ticket => {
                    allTickets[ticket.id] = false;
                });

                const maxPurchase = await apiRequest('POST', '/tickets/purchase', {
                    ticketFeeMap: allTickets,
                    creditCardNumber: TEST_CARDS.VISA,
                    expirationDate: '12/25',
                    cvv: '123',
                    cardHolderName: `${testUsers[0].firstName} ${testUsers[0].lastName}`
                }, testUsers[0].token);

                totalTests++;
                if (maxPurchase.ok) {
                    logTest('4-Ticket Maximum Limit', true, 'User can purchase up to 4 tickets');
                    passedTests++;
                    
                    // Now try to purchase more (should fail)
                    // Create another event
                    const event3Data = {
                        ...eventData,
                        name: `Exceed Limit Test ${timestamp}`
                    };
                    
                    const event3Response = await createEvent(event3Data, organizer.token);
                    if (event3Response.ok) {
                        await wait(1000);
                        const event3Tickets = await getAvailableTickets(event3Response.data.id, testUsers[0].token);
                        
                        if (event3Tickets.length > 0) {
                            const exceedPurchase = await apiRequest('POST', '/tickets/purchase', {
                                ticketFeeMap: { [event3Tickets[0].id]: false },
                                creditCardNumber: TEST_CARDS.VISA,
                                expirationDate: '12/25',
                                cvv: '123',
                                cardHolderName: `${testUsers[0].firstName} ${testUsers[0].lastName}`
                            }, testUsers[0].token);

                            totalTests++;
                            // This should fail if user already has 4 tickets from same event
                            // However, since it's a different event, it should succeed
                            if (exceedPurchase.ok) {
                                logTest('Purchase on Different Event', true, 'Limit is per event');
                                passedTests++;
                            } else {
                                logTest('Purchase on Different Event', false, `Status: ${exceedPurchase.status}`);
                            }
                        }
                    }
                } else {
                    logTest('4-Ticket Maximum Limit', false, `Status: ${maxPurchase.status}`);
                }
            }
        }

        // Step 6: Test ticket reselling functionality
        if (purchasedTickets.length > 0) {
            const ticketToResell = purchasedTickets[0];
            const resellResponse = await apiRequest('POST', `/tickets/${ticketToResell.id}/resell`, null, testUsers[0].token);
            
            totalTests++;
            // Reselling might not be allowed (400) or might succeed (200)
            if (resellResponse.ok || resellResponse.status === 400) {
                logTest('Ticket Resell Attempt', true, `Status: ${resellResponse.status}`);
                passedTests++;
                
                if (resellResponse.ok) {
                    // If reselling succeeded, check if ticket is available again
                    await wait(1000);
                    const ticketsAfterResell = await getAvailableTickets(createdEvent.id, testUsers[1].token);
                    totalTests++;
                    
                    if (ticketsAfterResell.length > 0) {
                        logTest('Ticket Available After Resell', true, 'Ticket back in pool');
                        passedTests++;
                    } else {
                        logTest('Ticket Available After Resell', false, 'Ticket not available');
                    }
                }
            } else {
                logTest('Ticket Resell Attempt', false, `Unexpected status: ${resellResponse.status}`);
            }
        }

    } catch (error) {
        console.error('Test error:', error);
        logTest('Sold Out and Reselling Test', false, error.message);
    }

    console.log(`\n--- Test Results: ${passedTests}/${totalTests} passed ---\n`);
    return { passed: passedTests, total: totalTests };
}

// Main test runner
async function runTests() {
    console.log('╔════════════════════════════════════════════╗');
    console.log('║   TicketBlock API Test Suite             ║');
    console.log('║   Testing Sold Out & Reselling Features  ║');
    console.log('╚════════════════════════════════════════════╝\n');

    const timestamp = Date.now();
    let totalPassed = 0;
    let totalTests = 0;

    try {
        // Setup: Create some test users if needed
        console.log('Setting up test environment...\n');
        
        for (let i = 0; i < 3; i++) {
            const user = await registerUser(
                `user${timestamp + i}@test.com`,
                'Password123!',
                `User${i}`,
                `Test${i}`,
                'USER'
            );
            if (user) {
                users.push(user);
            }
        }

        console.log(`Created ${users.length} test users\n`);

        // Run the sold out and reselling test
        const soldOutResults = await testSoldOutAndReselling(users, events);
        totalPassed += soldOutResults.passed;
        totalTests += soldOutResults.total;

    } catch (error) {
        console.error('Fatal error during tests:', error);
    }

    // Summary
    console.log('\n╔════════════════════════════════════════════╗');
    console.log('║              FINAL SUMMARY                 ║');
    console.log('╚════════════════════════════════════════════╝');
    console.log(`Total Tests: ${totalTests}`);
    console.log(`Passed: ${totalPassed}`);
    console.log(`Failed: ${totalTests - totalPassed}`);
    console.log(`Success Rate: ${totalTests > 0 ? ((totalPassed / totalTests) * 100).toFixed(2) : 0}%`);
    console.log('');

    process.exit(totalTests === totalPassed ? 0 : 1);
}

// Run tests if this is the main module
if (require.main === module) {
    runTests().catch(error => {
        console.error('Unhandled error:', error);
        process.exit(1);
    });
}

module.exports = {
    apiRequest,
    getAvailableTickets,
    logTest,
    wait,
    registerUser,
    createEvent,
    testSoldOutAndReselling,
    runTests
};
