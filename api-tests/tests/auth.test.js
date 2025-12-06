const { api, TestRunner, assert, config } = require('../test-utils');

async function runAuthTests() {
  const suite = new TestRunner('Authentication API Tests');

  // =====================
  // SIMPLE CASES
  // =====================

  suite.test('Register new user with valid data', async () => {
    const timestamp = Date.now();
    const response = await api.post('/auth/register', {
      firstName: 'Mario',
      lastName: 'Rossi',
      email: `mario.rossi.${timestamp}@test.com`,
      password: 'Password123!',
      role: 'USER'
    });
    
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.exists(response.data.token, 'Should return a token');
    assert.exists(response.data.user, 'Should return user data');
  });

  suite.test('Register new organizer with valid data', async () => {
    const timestamp = Date.now();
    const response = await api.post('/auth/register', {
      firstName: 'Giuseppe',
      lastName: 'Verdi',
      email: `giuseppe.verdi.${timestamp}@test.com`,
      password: 'Password123!',
      role: 'ORGANIZER'
    });
    
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.exists(response.data.token, 'Should return a token');
  });

  suite.test('Authenticate with valid credentials', async () => {
    // First register a user
    const timestamp = Date.now();
    const email = `auth.test.${timestamp}@test.com`;
    const password = 'Password123!';
    
    await api.post('/auth/register', {
      firstName: 'Test',
      lastName: 'User',
      email: email,
      password: password,
      role: 'USER'
    });

    // Then authenticate
    const response = await api.post('/auth/authenticate', {
      email: email,
      password: password
    });
    
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.exists(response.data.token, 'Should return a token');
    assert.exists(response.data.user, 'Should return user data');
  });

  // =====================
  // EDGE CASES
  // =====================

  suite.test('Register with invalid email format', async () => {
    try {
      await api.post('/auth/register', {
        firstName: 'Test',
        lastName: 'User',
        email: 'invalid-email',
        password: 'Password123!',
        role: 'USER'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  suite.test('Register with password too short (less than 8 characters)', async () => {
    try {
      const timestamp = Date.now();
      await api.post('/auth/register', {
        firstName: 'Test',
        lastName: 'User',
        email: `test.${timestamp}@test.com`,
        password: 'Pass1!',
        role: 'USER'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  suite.test('Register with ADMIN role (should be forbidden)', async () => {
    try {
      const timestamp = Date.now();
      await api.post('/auth/register', {
        firstName: 'Test',
        lastName: 'Admin',
        email: `admin.${timestamp}@test.com`,
        password: 'Password123!',
        role: 'ADMIN'
      });
      throw new Error('Should have failed');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 403, 
        'Should return 400 or 403 for ADMIN role');
    }
  });

  suite.test('Register with invalid role', async () => {
    try {
      const timestamp = Date.now();
      await api.post('/auth/register', {
        firstName: 'Test',
        lastName: 'User',
        email: `test.${timestamp}@test.com`,
        password: 'Password123!',
        role: 'INVALID_ROLE'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.truthy(error.response.status === 400 || error.response.status === 403,
        'Should return 400 or 403 for invalid role');
    }
  });

  suite.test('Register with missing required fields', async () => {
    try {
      await api.post('/auth/register', {
        firstName: 'Test',
        // Missing lastName, email, password, role
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  suite.test('Register with empty firstName', async () => {
    try {
      const timestamp = Date.now();
      await api.post('/auth/register', {
        firstName: '',
        lastName: 'User',
        email: `test.${timestamp}@test.com`,
        password: 'Password123!',
        role: 'USER'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  suite.test('Register with firstName exceeding 50 characters', async () => {
    try {
      const timestamp = Date.now();
      await api.post('/auth/register', {
        firstName: 'A'.repeat(51),
        lastName: 'User',
        email: `test.${timestamp}@test.com`,
        password: 'Password123!',
        role: 'USER'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  suite.test('Authenticate with wrong password', async () => {
    try {
      // Register a user first
      const timestamp = Date.now();
      const email = `wrong.pwd.${timestamp}@test.com`;
      
      await api.post('/auth/register', {
        firstName: 'Test',
        lastName: 'User',
        email: email,
        password: 'CorrectPassword123!',
        role: 'USER'
      });

      // Try to authenticate with wrong password
      await api.post('/auth/authenticate', {
        email: email,
        password: 'WrongPassword123!'
      });
      throw new Error('Should have failed with 401 Unauthorized');
    } catch (error) {
      assert.truthy(error.response.status === 401 || error.response.status === 403,
        'Should return 401 or 403 for wrong password');
    }
  });

  suite.test('Authenticate with non-existent email', async () => {
    try {
      await api.post('/auth/authenticate', {
        email: 'nonexistent@test.com',
        password: 'Password123!'
      });
      throw new Error('Should have failed with 401 Unauthorized');
    } catch (error) {
      assert.truthy(error.response.status === 401 || error.response.status === 403 || error.response.status === 404,
        'Should return 401, 403, or 404 for non-existent user');
    }
  });

  suite.test('Authenticate with invalid email format', async () => {
    try {
      await api.post('/auth/authenticate', {
        email: 'invalid-email',
        password: 'Password123!'
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  suite.test('Authenticate with empty credentials', async () => {
    try {
      await api.post('/auth/authenticate', {
        email: '',
        password: ''
      });
      throw new Error('Should have failed with 400 Bad Request');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  await suite.run();
}

// Run if executed directly
if (require.main === module) {
  runAuthTests().catch(console.error);
}

module.exports = runAuthTests;
