/**
 * EXAMPLE TEST FILE
 * 
 * Questo è un file di esempio che mostra come creare nuovi test
 * per estendere la suite di test TicketBlock.
 * 
 * Per usare questo template:
 * 1. Copia questo file e rinominalo (es. my-feature.test.js)
 * 2. Modifica il nome della suite e aggiungi i tuoi test
 * 3. Importa ed esegui il test nel test-runner.js
 */

const { api, TestRunner, assert, config } = require('../test-utils');

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
  // SIMPLE CASES
  // =====================

  suite.test('Example: Simple GET request', async () => {
    const response = await api.get('/events');
    
    // Assertions disponibili:
    assert.equal(response.status, 200, 'Should return 200 OK');
    assert.truthy(Array.isArray(response.data), 'Should return an array');
  });

  suite.test('Example: POST request with authentication', async () => {
    const token = await createTestUser();
    global.authToken = token; // Imposta il token per richieste autenticate

    try {
      const response = await api.get('/tickets/mine');
      
      assert.equal(response.status, 200, 'Should return 200 OK');
      assert.exists(response.data, 'Should return data');
    } finally {
      global.authToken = null; // Pulisci il token
    }
  });

  // =====================
  // EDGE CASES
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
        // Missing required fields
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
// ASSERTIONS DISPONIBILI
// =====================

/*
  assert.equal(actual, expected, message)
    - Verifica che due valori siano uguali
    
  assert.notEqual(actual, expected, message)
    - Verifica che due valori siano diversi
    
  assert.truthy(value, message)
    - Verifica che il valore sia truthy
    
  assert.falsy(value, message)
    - Verifica che il valore sia falsy
    
  assert.exists(value, message)
    - Verifica che il valore non sia null o undefined
    
  assert.arrayContains(array, item, message)
    - Verifica che l'array contenga l'item
    
  assert.throws(fn, message)
    - Verifica che la funzione generi un errore
*/

// =====================
// SUGGERIMENTI
// =====================

/*
  1. Usa try-catch per test che devono fallire
  2. Imposta global.authToken per richieste autenticate
  3. Pulisci sempre global.authToken in finally
  4. Usa timestamp per creare dati univoci
  5. Testa sia casi di successo che di fallimento
  6. Aggiungi messaggi descrittivi alle assertions
  7. Raggruppa test simili insieme
  8. Usa helper functions per codice ripetuto
*/

// Run if executed directly
if (require.main === module) {
  runExampleTests().catch(console.error);
}

module.exports = runExampleTests;
