/**
 * Test script per l'acquisto di biglietti lato Organizer
 * 
 * Questo script testa il flusso dell'organizzatore:
 * 1. Registrazione come ORGANIZER
 * 2. Login e ottenimento del token JWT
 * 3. Creazione di un evento (che genera automaticamente i biglietti)
 * 4. Visualizzazione dell'evento creato
 * 5. Eliminazione dell'evento
 */

const BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';
const API_V1 = `${BASE_URL}/api/v1`;

// Genera email unica per evitare conflitti
const timestamp = Date.now();
const organizerEmail = `organizer_${timestamp}@test.com`;
const organizerPassword = 'password123';

// Utility per fare richieste HTTP
async function httpRequest(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        },
        ...options
    });

    const data = await response.json().catch(() => null);

    return {
        status: response.status,
        ok: response.ok,
        data
    };
}

// Test: Registrazione Organizer
async function testOrganizerRegistration() {
    console.log('\n=== Test 1: Registrazione Organizer ===');

    const response = await httpRequest(`${API_V1}/auth/register`, {
        method: 'POST',
        body: JSON.stringify({
            firstName: 'Test',
            lastName: 'Organizer',
            email: organizerEmail,
            password: organizerPassword,
            role: 'ORGANIZER'
        })
    });

    if (response.ok && response.data?.token) {
        console.log('✅ Registrazione Organizer riuscita');
        console.log(`   Email: ${organizerEmail}`);
        console.log(`   Token ricevuto: ${response.data.token.substring(0, 20)}...`);
        return response.data.token;
    } else {
        console.log('❌ Registrazione Organizer fallita');
        console.log(`   Status: ${response.status}`);
        console.log(`   Response: ${JSON.stringify(response.data)}`);
        return null;
    }
}

// Test: Login Organizer
async function testOrganizerLogin() {
    console.log('\n=== Test 2: Login Organizer ===');

    const response = await httpRequest(`${API_V1}/auth/authenticate`, {
        method: 'POST',
        body: JSON.stringify({
            email: organizerEmail,
            password: organizerPassword
        })
    });

    if (response.ok && response.data?.token) {
        console.log('✅ Login Organizer riuscito');
        console.log(`   Token ricevuto: ${response.data.token.substring(0, 20)}...`);
        return response.data.token;
    } else {
        console.log('❌ Login Organizer fallito');
        console.log(`   Status: ${response.status}`);
        console.log(`   Response: ${JSON.stringify(response.data)}`);
        return null;
    }
}

// Test: Creazione Evento (genera biglietti automaticamente)
async function testCreateEvent(token) {
    console.log('\n=== Test 3: Creazione Evento (Acquisto/Generazione Biglietti) ===');

    // Data futura per l'evento
    const eventDate = new Date();
    eventDate.setDate(eventDate.getDate() + 30); // 30 giorni nel futuro
    const dateStr = eventDate.toISOString().split('T')[0]; // YYYY-MM-DD

    const eventData = {
        name: `Concerto Test ${timestamp}`,
        date: dateStr,
        startTime: '20:00:00',
        endTime: '23:00:00',
        imageUrl: 'https://example.com/concert.jpg',
        venueId: 1, // Assumendo che il venue con ID 1 esista
        standardTicketPrice: 50.00,
        vipTicketPrice: 100.00
    };

    const response = await httpRequest(`${API_V1}/events`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(eventData)
    });

    if (response.ok && response.data?.id) {
        console.log('✅ Evento creato con successo');
        console.log(`   ID Evento: ${response.data.id}`);
        console.log(`   Nome: ${response.data.name}`);
        console.log(`   Data: ${response.data.date}`);
        console.log(`   Venue: ${response.data.venue?.name || 'N/A'}`);
        console.log(`   Prezzo Standard: €${response.data.standardTicketPrice}`);
        console.log(`   Prezzo VIP: €${response.data.vipTicketPrice}`);
        console.log('   📝 I biglietti sono stati generati automaticamente per tutti i posti del venue');
        return response.data.id;
    } else {
        console.log('❌ Creazione evento fallita');
        console.log(`   Status: ${response.status}`);
        console.log(`   Response: ${JSON.stringify(response.data)}`);
        return null;
    }
}

// Test: Visualizza Evento
async function testGetEvent(token, eventId) {
    console.log('\n=== Test 4: Visualizzazione Evento Creato ===');

    const response = await httpRequest(`${API_V1}/events/${eventId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok && response.data) {
        console.log('✅ Evento recuperato con successo');
        console.log(`   ID: ${response.data.id}`);
        console.log(`   Nome: ${response.data.name}`);
        console.log(`   Organizzatore: ${response.data.organizer?.firstName} ${response.data.organizer?.lastName}`);
        return true;
    } else {
        console.log('❌ Recupero evento fallito');
        console.log(`   Status: ${response.status}`);
        return false;
    }
}

// Test: Lista tutti gli eventi
async function testGetAllEvents(token) {
    console.log('\n=== Test 5: Lista Tutti gli Eventi ===');

    const response = await httpRequest(`${API_V1}/events`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok && Array.isArray(response.data)) {
        console.log('✅ Lista eventi recuperata');
        console.log(`   Numero totale eventi: ${response.data.length}`);
        response.data.forEach((event, index) => {
            console.log(`   ${index + 1}. ${event.name} (ID: ${event.id})`);
        });
        return true;
    } else {
        console.log('❌ Recupero lista eventi fallito');
        console.log(`   Status: ${response.status}`);
        return false;
    }
}

// Test: Elimina Evento
async function testDeleteEvent(token, eventId) {
    console.log('\n=== Test 6: Eliminazione Evento ===');

    const response = await httpRequest(`${API_V1}/events/${eventId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.status === 204 || response.ok) {
        console.log('✅ Evento eliminato con successo');
        console.log(`   ID Evento eliminato: ${eventId}`);
        return true;
    } else {
        console.log('❌ Eliminazione evento fallita');
        console.log(`   Status: ${response.status}`);
        return false;
    }
}

// Main: Esegui tutti i test
async function runTests() {
    console.log('╔════════════════════════════════════════════════════════════╗');
    console.log('║     TEST ACQUISTO BIGLIETTI - LATO ORGANIZER               ║');
    console.log('╚════════════════════════════════════════════════════════════╝');
    console.log(`\nBase URL: ${BASE_URL}`);
    console.log('Questo script testa il flusso dell\'organizzatore per la');
    console.log('creazione di eventi e la generazione automatica dei biglietti.\n');

    let passed = 0;
    let failed = 0;

    try {
        // Test 1: Registrazione
        const regToken = await testOrganizerRegistration();
        if (regToken) passed++; else failed++;

        // Test 2: Login
        const loginToken = await testOrganizerLogin();
        if (loginToken) passed++; else failed++;

        const token = loginToken || regToken;
        if (!token) {
            console.log('\n❌ Impossibile continuare senza token valido');
            return;
        }

        // Test 3: Creazione evento (con generazione biglietti)
        const eventId = await testCreateEvent(token);
        if (eventId) passed++; else failed++;

        if (eventId) {
            // Test 4: Visualizza evento
            const getResult = await testGetEvent(token, eventId);
            if (getResult) passed++; else failed++;

            // Test 5: Lista eventi
            const listResult = await testGetAllEvents(token);
            if (listResult) passed++; else failed++;

            // Test 6: Elimina evento
            const deleteResult = await testDeleteEvent(token, eventId);
            if (deleteResult) passed++; else failed++;
        }

    } catch (error) {
        console.log(`\n❌ Errore durante l'esecuzione dei test: ${error.message}`);
        failed++;
    }

    console.log('\n╔════════════════════════════════════════════════════════════╗');
    console.log('║                    RIEPILOGO TEST                          ║');
    console.log('╚════════════════════════════════════════════════════════════╝');
    console.log(`   ✅ Test passati: ${passed}`);
    console.log(`   ❌ Test falliti: ${failed}`);
    console.log(`   📊 Totale: ${passed + failed}`);

    if (failed === 0) {
        console.log('\n🎉 Tutti i test sono passati con successo!');
    } else {
        console.log('\n⚠️  Alcuni test sono falliti. Verificare la configurazione del server.');
    }

    process.exit(failed > 0 ? 1 : 0);
}

runTests().catch(console.error);
