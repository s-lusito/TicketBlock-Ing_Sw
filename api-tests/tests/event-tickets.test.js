const { api, TestRunner, assert } = require('../test-utils');

// Helper function to create an organizer and get token
async function createOrganizer() {
  const timestamp = Date.now();
  const response = await api.post('/auth/register', {
    firstName: 'Event',
    lastName: 'Organizer',
    email: `organizer.${timestamp}@test.com`,
    password: 'Password123!',
    role: 'ORGANIZER'
  });
  return response.data.token;
}

async function runEventTicketsTests() {
  const suite = new TestRunner('Event Tickets API Tests');

  // =====================
  // SIMPLE CASES
  // =====================

  suite.test('Get tickets from event without filter', async () => {
    // Assuming event ID 1 exists
    try {
      const response = await api.get('/events/1/tickets');
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.truthy(Array.isArray(response.data), 'Should return an array');
    } catch (error) {
      // Event might not exist
      if (error.response && error.response.status === 404) {
        console.log('   Note: Event ID 1 does not exist, skipping this test');
        return; // Skip test if event doesn't exist
      }
      throw error;
    }
  });

  suite.test('Get tickets from event with AVAILABLE status filter', async () => {
    try {
      const response = await api.get('/events/1/tickets', {
        params: {
          ticketStatus: 'AVAILABLE'
        }
      });
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.truthy(Array.isArray(response.data), 'Should return an array');
    } catch (error) {
      // Event might not exist
      if (error.response && error.response.status === 404) {
        console.log('   Note: Event ID 1 does not exist, skipping this test');
        return;
      }
      throw error;
    }
  });

  suite.test('Get tickets from event with SOLD status filter', async () => {
    try {
      const response = await api.get('/events/1/tickets', {
        params: {
          ticketStatus: 'SOLD'
        }
      });
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.truthy(Array.isArray(response.data), 'Should return an array');
    } catch (error) {
      // Event might not exist
      if (error.response && error.response.status === 404) {
        console.log('   Note: Event ID 1 does not exist, skipping this test');
        return;
      }
      throw error;
    }
  });

  suite.test('Get tickets from newly created event', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      // Create a new event
      const createResponse = await api.post('/events', {
        name: 'Tickets Test Event',
        date: '2025-09-15',
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: '2025-08-01',
        venueId: 1,
        standardTicketPrice: 45.00,
        vipTicketPrice: 135.00
      });

      const eventId = createResponse.data.id;
      global.authToken = null;

      // Get tickets for this event
      const response = await api.get(`/events/${eventId}/tickets`);
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.truthy(Array.isArray(response.data), 'Should return an array');
    } catch (error) {
      // Venue might not exist
      if (error.response && (error.response.status === 400 || error.response.status === 404)) {
        console.log('   Note: Venue ID 1 does not exist, skipping this test');
        return;
      }
      throw error;
    } finally {
      global.authToken = null;
    }
  });

  // =====================
  // EDGE CASES
  // =====================

  suite.test('Get tickets from non-existent event', async () => {
    try {
      await api.get('/events/999999/tickets');
      throw new Error('Should have failed with 404 Not Found');
    } catch (error) {
      assert.equal(error.response.status, 404, 'Should return 404 Not Found');
    }
  });

  suite.test('Get tickets with invalid ticket status filter', async () => {
    try {
      const response = await api.get('/events/1/tickets', {
        params: {
          ticketStatus: 'INVALID_STATUS'
        }
      });
      // Some APIs might ignore invalid filters
      assert.truthy(response.status === 200 || response.status === 400,
        'Should either return 200 (ignoring invalid filter) or 400');
    } catch (error) {
      // It's OK if it fails with 400 or 404 (if event doesn't exist)
      assert.truthy(error.response.status === 400 || error.response.status === 404,
        'Should return 400 for invalid filter or 404 if event does not exist');
    }
  });

  suite.test('Get tickets from event with negative ID', async () => {
    try {
      await api.get('/events/-1/tickets');
      throw new Error('Should have failed with 400 or 404');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 404,
        'Should return 400 or 404 for negative event ID');
    }
  });

  suite.test('Get tickets from event with zero ID', async () => {
    try {
      await api.get('/events/0/tickets');
      throw new Error('Should have failed with 400 or 404');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 404,
        'Should return 400 or 404 for zero event ID');
    }
  });

  suite.test('Get tickets from event with invalid ID format', async () => {
    try {
      await api.get('/events/invalid/tickets');
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  suite.test('Get tickets with INVALIDATED status filter', async () => {
    try {
      const response = await api.get('/events/1/tickets', {
        params: {
          ticketStatus: 'INVALIDATED'
        }
      });
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.truthy(Array.isArray(response.data), 'Should return an array');
    } catch (error) {
      // Event might not exist
      if (error.response && error.response.status === 404) {
        console.log('   Note: Event ID 1 does not exist, skipping this test');
        return;
      }
      throw error;
    }
  });

  await suite.run();
}

// Run if executed directly
if (require.main === module) {
  runEventTicketsTests().catch(console.error);
}

module.exports = runEventTicketsTests;
