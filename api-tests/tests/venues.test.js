const { api, TestRunner, assert } = require('../test-utils');

async function runVenueTests() {
  const suite = new TestRunner('Venue API Tests');

  // =====================
  // SIMPLE CASES
  // =====================

  suite.test('Get venue by valid ID', async () => {
    // Assuming venue ID 1 exists (typically created in database initialization)
    const response = await api.get('/venues/1');
    
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.exists(response.data.id, 'Should return venue with ID');
    assert.exists(response.data.name, 'Should return venue with name');
  });

  // =====================
  // EDGE CASES
  // =====================

  suite.test('Get venue with non-existent ID', async () => {
    try {
      await api.get('/venues/999999');
      throw new Error('Should have failed with 404 Not Found');
    } catch (error) {
      assert.equal(error.response.status, 404, 'Should return 404 Not Found');
    }
  });

  suite.test('Get venue with negative ID', async () => {
    try {
      await api.get('/venues/-1');
      throw new Error('Should have failed with 400 or 404');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 404,
        'Should return 400 or 404 for negative ID');
    }
  });

  suite.test('Get venue with zero ID', async () => {
    try {
      await api.get('/venues/0');
      throw new Error('Should have failed with 400 or 404');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 404,
        'Should return 400 or 404 for zero ID');
    }
  });

  suite.test('Get venue with invalid ID format (string)', async () => {
    try {
      await api.get('/venues/invalid');
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  suite.test('Get venue with very large ID', async () => {
    try {
      await api.get('/venues/2147483648'); // Max int + 1
      throw new Error('Should have failed with 400 or 404');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 404,
        'Should return 400 or 404 for very large ID');
    }
  });

  await suite.run();
}

// Run if executed directly
if (require.main === module) {
  runVenueTests().catch(console.error);
}

module.exports = runVenueTests;
