/**
 * EXAMPLE TEST FILE / FILE DI ESEMPIO TEST
 * 
 * This is an example file showing how to create new tests to extend the TicketBlock test suite.
 * Questo è un file di esempio che mostra come creare nuovi test per estendere la suite di test TicketBlock.
 * 
 * To use this template / Per usare questo template:
 * 1. Copy this file and rename it (e.g., my-feature.test.js)
 *    Copia questo file e rinominalo (es. my-feature.test.js)
 * 2. Modify the suite name and add your tests
 *    Modifica il nome della suite e aggiungi i tuoi test
 * 3. Import and run the test in test-runner.js
 *    Importa ed esegui il test nel test-runner.js
 */

const { api, TestRunner, assert, config } = require('../test-utils');

// Helper function to create a test user (example)
// Helper function per creare un utente di test (esempio)
async function createTestUser() {
  const timestamp = Date.now();
  const response = await api.post('/auth/register', {
    firstName: 'Example',
    lastName: 'User',
    email: `example.${timestamp}@test.com`,
    password: 'Password123!',
    role: 'USER'
  });
  return response.data.token;
}

async function runExampleTests() {
  const suite = new TestRunner('Example Test Suite');

  // =====================
  // SIMPLE CASES / CASI SEMPLICI
  // =====================

  suite.test('Example: Simple GET request', async () => {
    const response = await api.get('/events');
    
    // Available assertions / Assertions disponibili:
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.truthy(Array.isArray(response.data), 'Should return an array');
  });

  suite.test('Example: POST request with authentication', async () => {
    const token = await createTestUser();
    global.authToken = token; // Set token for authenticated requests / Imposta il token per richieste autenticate

    try {
      const response = await api.get('/tickets/mine');
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.exists(response.data, 'Should return data');
    } finally {
      global.authToken = null; // Clean up token / Pulisci il token
    }
  });

  // =====================
  // EDGE CASES / CASI LIMITE
  // =====================

  suite.test('Example: Request that should fail', async () => {
    try {
      await api.get('/non-existent-endpoint');
      throw new Error('Should have failed with 404');
    } catch (error) {
      assert.equal(error.response.status, 404, 'Should return 404 Not Found');
    }
  });

  suite.test('Example: Testing invalid input', async () => {
    try {
      await api.post('/events', {
        // Missing required fields / Campi richiesti mancanti
        name: ''
      });
      throw new Error('Should have failed with 400');
    } catch (error) {
      assert.equal(error.response.status, 400, 'Should return 400 Bad Request');
    }
  });

  await suite.run();
}

// =====================
// AVAILABLE ASSERTIONS / ASSERTIONS DISPONIBILI
// =====================

/*
  assert.equal(actual, expected, message)
    - Verify that two values are equal
    - Verifica che due valori siano uguali
    
  assert.notEqual(actual, expected, message)
    - Verify that two values are different
    - Verifica che due valori siano diversi
    
  assert.truthy(value, message)
    - Verify that the value is truthy
    - Verifica che il valore sia truthy
    
  assert.falsy(value, message)
    - Verify that the value is falsy
    - Verifica che il valore sia falsy
    
  assert.exists(value, message)
    - Verify that the value is not null or undefined
    - Verifica che il valore non sia null o undefined
    
  assert.arrayContains(array, item, message)
    - Verify that the array contains the item
    - Verifica che l'array contenga l'item
    
  assert.throws(fn, message)
    - Verify that the function throws an error
    - Verifica che la funzione generi un errore
*/

// =====================
// TIPS / SUGGERIMENTI
// =====================

/*
  1. Use try-catch for tests that should fail / Usa try-catch per test che devono fallire
  2. Set global.authToken for authenticated requests / Imposta global.authToken per richieste autenticate
  3. Always clean up global.authToken in finally / Pulisci sempre global.authToken in finally
  4. Use timestamp to create unique data / Usa timestamp per creare dati univoci
  5. Test both success and failure cases / Testa sia casi di successo che di fallimento
  6. Add descriptive messages to assertions / Aggiungi messaggi descrittivi alle assertions
  7. Group similar tests together / Raggruppa test simili insieme
  8. Use helper functions for repeated code / Usa helper functions per codice ripetuto
*/

// Run if executed directly
if (require.main === module) {
  runExampleTests().catch(console.error);
}

module.exports = runExampleTests;
