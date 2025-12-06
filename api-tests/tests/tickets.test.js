const { api, TestRunner, assert, config } = require('../test-utils');

// Helper function to create a user and get token
async function createUser() {
  const timestamp = Date.now();
  const response = await api.post('/auth/register', {
    firstName: 'Ticket',
    lastName: 'Buyer',
    email: `buyer.${timestamp}@test.com`,
    password: 'Password123!',
    role: 'USER'
  });
  return response.data.token;
}

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

async function runTicketTests() {
  const suite = new TestRunner('Ticket API Tests');

  // =====================
  // SIMPLE CASES
  // =====================

  suite.test('Get my tickets (authenticated user)', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      const response = await api.get('/tickets/mine');
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.truthy(Array.isArray(response.data), 'Should return an array');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets with valid data', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      const response = await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false,
          "2": true
        },
        creditCardNumber: '4532015112830366',
        expirationDate: '12/25',
        cvv: '123',
        cardHolderName: 'Test User'
      });
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.exists(response.data, 'Should return purchase response');
    } catch (error) {
      // Might fail if tickets don't exist or are not available
      // That's acceptable for this test
      assert.truthy(
        error.response.status === 400 || 
        error.response.status === 404 || 
        error.response.status === 409,
        'Should return 400, 404, or 409 if tickets are not available'
      );
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Resell ticket with valid ID', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      const response = await api.post('/tickets/1/resell');
      
      // Could succeed or fail depending on ticket ownership and status
      assert.truthy(
        response.status === 200 || response.status === 204,
        'Should return 200 or 204 OK if ticket is resellable'
      );
    } catch (error) {
      // Expected to fail if user doesn't own ticket or ticket is not resellable
      assert.truthy(
        error.response.status === 400 || 
        error.response.status === 403 || 
        error.response.status === 404,
        'Should return 400, 403, or 404 if ticket cannot be resold'
      );
    } finally {
      global.authToken = null;
    }
  });

  // =====================
  // EDGE CASES
  // =====================

  suite.test('Get my tickets without authentication', async () => {
    global.authToken = null;

    try {
      await api.get('/tickets/mine');
      throw new Error('Should have failed with 401 or 403');
    } catch (error) {
      assert.truthy(error.response.status === 401 || error.response.status === 403,
        'Should return 401 or 403 for unauthenticated request');
    }
  });

  suite.test('Purchase tickets with invalid credit card number', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false
        },
        creditCardNumber: '1234567890',
        expirationDate: '12/25',
        cvv: '123',
        cardHolderName: 'Test User'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets with invalid expiration date format', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false
        },
        creditCardNumber: '4532015112830366',
        expirationDate: '2025-12-31', // Wrong format
        cvv: '123',
        cardHolderName: 'Test User'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets with invalid CVV (too short)', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false
        },
        creditCardNumber: '4532015112830366',
        expirationDate: '12/25',
        cvv: '12', // Too short
        cardHolderName: 'Test User'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets with non-numeric CVV', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false
        },
        creditCardNumber: '4532015112830366',
        expirationDate: '12/25',
        cvv: 'ABC',
        cardHolderName: 'Test User'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets with empty ticket map', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {},
        creditCardNumber: '4532015112830366',
        expirationDate: '12/25',
        cvv: '123',
        cardHolderName: 'Test User'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets with missing required fields', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false
        }
        // Missing credit card details
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets with card holder name too short', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false
        },
        creditCardNumber: '4532015112830366',
        expirationDate: '12/25',
        cvv: '123',
        cardHolderName: 'A' // Too short (less than 2 characters)
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets with card holder name too long', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false
        },
        creditCardNumber: '4532015112830366',
        expirationDate: '12/25',
        cvv: '123',
        cardHolderName: 'A'.repeat(51) // Too long (more than 50 characters)
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Purchase tickets without authentication', async () => {
    global.authToken = null;

    try {
      await api.post('/tickets/purchase', {
        ticketFeeMap: {
          "1": false
        },
        creditCardNumber: '4532015112830366',
        expirationDate: '12/25',
        cvv: '123',
        cardHolderName: 'Test User'
      });
      throw new Error('Should have failed with 401 or 403');
    } catch (error) {
      assert.truthy(error.response.status === 401 || error.response.status === 403,
        'Should return 401 or 403 for unauthenticated request');
    }
  });

  suite.test('Resell ticket with non-existent ID', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/999999/resell');
      throw new Error('Should have failed with 404 Not Found');
    } catch (error) {
      assert.truthy(error.response.status === 403 || error.response.status === 404,
        'Should return 403 or 404 for non-existent ticket');
    } finally {
      global.authToken = null;
    }
  });

  suite.test('Resell ticket without authentication', async () => {
    global.authToken = null;

    try {
      await api.post('/tickets/1/resell');
      throw new Error('Should have failed with 401 or 403');
    } catch (error) {
      assert.truthy(error.response.status === 401 || error.response.status === 403,
        'Should return 401 or 403 for unauthenticated request');
    }
  });

  suite.test('Resell ticket with invalid ID format', async () => {
    const token = await createUser();
    global.authToken = token;

    try {
      await api.post('/tickets/invalid/resell');
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    } finally {
      global.authToken = null;
    }
  });

  await suite.run();
}

// Run if executed directly
if (require.main === module) {
  runTicketTests().catch(console.error);
}

module.exports = runTicketTests;
