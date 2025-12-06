const { api, TestRunner, assert, config } = require('../test-utils');

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

async function runEventTests() {
  const suite = new TestRunner('Event API Tests');

  // =====================
  // SIMPLE CASES
  // =====================

  suite.test('Get all events without filters', async () => {
    const response = await api.get('/events');
    
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.truthy(Array.isArray(response.data), 'Should return an array');
  });

  suite.test('Get all events with sale status filter', async () => {
    const response = await api.get('/events', {
      params: {
        saleStatus: ['ONGOING', 'NOT_STARTED']
      }
    });
    
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.truthy(Array.isArray(response.data), 'Should return an array');
  });

  suite.test('Create event with valid data (as organizer)', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      const response = await api.post('/events', {
        name: 'Concerto di Primavera',
        date: '2025-06-15',
        startTime: '20:00:00',
        endTime: '23:00:00',
        imageUrl: 'https://example.com/image.jpg',
        saleStartDate: '2025-05-01',
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
      });
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.exists(response.data.id, 'Should return event with ID');
      assert.equal(response.data.name, 'Concerto di Primavera', 'Event name should match');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Get event by ID', async () => {
    // First create an event
    const token = await createOrganizer();
    global.authToken = token;

    const createResponse = await api.post('/events', {
      name: 'Test Event',
      date: '2025-07-20',
      startTime: '19:00:00',
      endTime: '22:00:00',
      saleStartDate: '2025-06-01',
      venueId: 1,
      standardTicketPrice: 40.00,
      vipTicketPrice: 120.00
    });

    const eventId = createResponse.data.id;
    global.authToken = null;

    // Then get it by ID
    const response = await api.get(`/events/${eventId}`);
    
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.equal(response.data.id, eventId, 'Should return correct event');
    assert.equal(response.data.name, 'Test Event', 'Event name should match');
  });

  suite.test('Delete event (as organizer)', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      // Create an event
      const createResponse = await api.post('/events', {
        name: 'Event to Delete',
        date: '2025-08-10',
        startTime: '18:00:00',
        endTime: '21:00:00',
        saleStartDate: '2025-07-01',
        venueId: 1,
        standardTicketPrice: 35.00,
        vipTicketPrice: 100.00
      });

      const eventId = createResponse.data.id;

      // Delete it
      const response = await api.delete(`/events/${eventId}`);
      assert.equal(response.status, 204, 'Should return 204 No Content');
    } finally {
      global.authToken = null;
    }
  });

  // =====================
  // EDGE CASES
  // =====================

  suite.test('Create event with missing required fields', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      await api.post('/events', {
        name: 'Incomplete Event',
        // Missing date, startTime, endTime, saleStartDate, venueId, prices
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Create event with empty name', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      await api.post('/events', {
        name: '',
        date: '2025-06-15',
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: '2025-05-01',
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Create event with name exceeding 50 characters', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      await api.post('/events', {
        name: 'A'.repeat(51),
        date: '2025-06-15',
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: '2025-05-01',
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Create event with end time before start time', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      await api.post('/events', {
        name: 'Invalid Time Event',
        date: '2025-06-15',
        startTime: '23:00:00',
        endTime: '20:00:00', // Before start time
        saleStartDate: '2025-05-01',
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 422,
        'Should return 400 or 422 for invalid times');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Create event with negative ticket price', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      await api.post('/events', {
        name: 'Negative Price Event',
        date: '2025-06-15',
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: '2025-05-01',
        venueId: 1,
        standardTicketPrice: -50.00,
        vipTicketPrice: 150.00
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Create event with zero ticket price', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      await api.post('/events', {
        name: 'Zero Price Event',
        date: '2025-06-15',
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: '2025-05-01',
        venueId: 1,
        standardTicketPrice: 0.00,
        vipTicketPrice: 150.00
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Create event with non-existent venue ID', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      await api.post('/events', {
        name: 'Invalid Venue Event',
        date: '2025-06-15',
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: '2025-05-01',
        venueId: 999999,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
      });
      throw new Error('Should have failed');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 404,
        'Should return 400 or 404 for non-existent venue');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Get event with non-existent ID', async () => {
    try {
      await api.get('/events/999999');
      throw new Error('Should have failed with 404 Not Found');
    } catch (error) {
      assert.equal(error.response.status, 404, 'Should return 404 Not Found');
    }
  });

  suite.test('Delete event with non-existent ID', async () => {
    const token = await createOrganizer();
    global.authToken = token;

    try {
      await api.delete('/events/999999');
      throw new Error('Should have failed with 404 Not Found');
    } catch (error) {
      assert.equal(error.response.status, 404, 'Should return 404 Not Found');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Create event without authentication', async () => {
    global.authToken = null;

    try {
      await api.post('/events', {
        name: 'Unauthorized Event',
        date: '2025-06-15',
        startTime: '20:00:00',
        endTime: '23:00:00',
        saleStartDate: '2025-05-01',
        venueId: 1,
        standardTicketPrice: 50.00,
        vipTicketPrice: 150.00
      });
      throw new Error('Should have failed with 401 or 403');
    } catch (error) {
      assert.truthy(error.response.status === 401 || error.response.status === 403,
        'Should return 401 or 403 for unauthenticated request');
    }
  });

  suite.test('Get all events with invalid sale status filter', async () => {
    try {
      const response = await api.get('/events', {
        params: {
          saleStatus: ['INVALID_STATUS']
        }
      });
      // Some APIs might ignore invalid filters, so check if it returns successfully or fails
      assert.truthy(response.status === 200 || response.status === 400,
        'Should either return 200 (ignoring invalid filter) or 400');
    } catch (error) {
      // It's OK if it fails with 400
      assert.equal(error.response.status, 400, 'Should return 400 for invalid filter');
    }
  });

  await suite.run();
}

// Run if executed directly
if (require.main === module) {
  runEventTests().catch(console.error);
}

module.exports = runEventTests;
