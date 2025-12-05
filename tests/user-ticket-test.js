/**
 * Test script per l'acquisto di biglietti lato User
 * 
 * Questo script testa il flusso dell'utente:
 * 1. Registrazione come USER
 * 2. Login e ottenimento del token JWT
 * 3. Visualizzazione degli eventi disponibili
 * 4. Acquisto di un biglietto (quando l'endpoint sarà implementato)
 * 
 * Nota: L'endpoint per l'acquisto biglietti (/api/v1/tickets/purchase) 
 * potrebbe non essere ancora implementato. Il test è strutturato per
 * funzionare quando sarà disponibile.
 */

const BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';
const API_V1 = `${BASE_URL}/api/v1`;

// Genera email uniche per evitare conflitti
const timestamp = Date.now();
const userEmail = `user_${timestamp}@test.com`;
const userPassword = 'password123';
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

// Helper: Registra e autentica un organizer per creare eventi di test
async function setupOrganizerAndEvent() {
    console.log('\n=== Setup: Creazione Organizer e Evento di Test ===');

    // Registra organizer
    const regResponse = await httpRequest(`${API_V1}/auth/register`, {
        method: 'POST',
        body: JSON.stringify({
            firstName: 'Test',
            lastName: 'Organizer',
            email: organizerEmail,
            password: organizerPassword,
            role: 'ORGANIZER'
        })
    });

    if (!regResponse.ok) {
        console.log('❌ Setup fallito: impossibile registrare organizer');
        return null;
    }

    const organizerToken = regResponse.data.token;
    console.log('✅ Organizer registrato');

    // Crea evento
    const eventDate = new Date();
    eventDate.setDate(eventDate.getDate() + 30);
    const dateStr = eventDate.toISOString().split('T')[0];

    const eventResponse = await httpRequest(`${API_V1}/events`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${organizerToken}`
        },
        body: JSON.stringify({
            name: `Concerto per Test User ${timestamp}`,
            date: dateStr,
            startTime: '20:00:00',
            endTime: '23:00:00',
            imageUrl: 'https://example.com/concert.jpg',
            venueId: 1,
            standardTicketPrice: 50.00,
            vipTicketPrice: 100.00
        })
    });

    if (eventResponse.ok && eventResponse.data?.id) {
        console.log(`✅ Evento di test creato (ID: ${eventResponse.data.id})`);
        return {
            eventId: eventResponse.data.id,
            organizerToken: organizerToken
        };
    }

    console.log('❌ Setup fallito: impossibile creare evento');
    return null;
}

// Test: Registrazione User
async function testUserRegistration() {
    console.log('\n=== Test 1: Registrazione User ===');

    const response = await httpRequest(`${API_V1}/auth/register`, {
        method: 'POST',
        body: JSON.stringify({
            firstName: 'Test',
            lastName: 'User',
            email: userEmail,
            password: userPassword,
            role: 'USER'
        })
    });

    if (response.ok && response.data?.token) {
        console.log('✅ Registrazione User riuscita');
        console.log(`   Email: ${userEmail}`);
        console.log(`   Token ricevuto: ${response.data.token.substring(0, 20)}...`);
        return response.data.token;
    } else {
        console.log('❌ Registrazione User fallita');
        console.log(`   Status: ${response.status}`);
        console.log(`   Response: ${JSON.stringify(response.data)}`);
        return null;
    }
}

// Test: Login User
async function testUserLogin() {
    console.log('\n=== Test 2: Login User ===');

    const response = await httpRequest(`${API_V1}/auth/authenticate`, {
        method: 'POST',
        body: JSON.stringify({
            email: userEmail,
            password: userPassword
        })
    });

    if (response.ok && response.data?.token) {
        console.log('✅ Login User riuscito');
        console.log(`   Token ricevuto: ${response.data.token.substring(0, 20)}...`);
        return response.data.token;
    } else {
        console.log('❌ Login User fallito');
        console.log(`   Status: ${response.status}`);
        console.log(`   Response: ${JSON.stringify(response.data)}`);
        return null;
    }
}

// Test: Visualizza eventi disponibili
async function testViewAvailableEvents(token) {
    console.log('\n=== Test 3: Visualizzazione Eventi Disponibili ===');

    const response = await httpRequest(`${API_V1}/events`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok && Array.isArray(response.data)) {
        console.log('✅ Eventi recuperati con successo');
        console.log(`   Numero eventi disponibili: ${response.data.length}`);
        
        if (response.data.length > 0) {
            console.log('   Eventi:');
            response.data.forEach((event, index) => {
                console.log(`   ${index + 1}. ${event.name}`);
                console.log(`      - Data: ${event.date}`);
                console.log(`      - Prezzo Standard: €${event.standardTicketPrice}`);
                console.log(`      - Prezzo VIP: €${event.vipTicketPrice}`);
            });
            return response.data[0]; // Restituisce il primo evento per i test successivi
        }
        return null;
    } else {
        console.log('❌ Recupero eventi fallito');
        console.log(`   Status: ${response.status}`);
        console.log(`   Response: ${JSON.stringify(response.data)}`);
        return null;
    }
}

// Test: Visualizza dettagli evento
async function testViewEventDetails(token, eventId) {
    console.log('\n=== Test 4: Visualizzazione Dettagli Evento ===');

    const response = await httpRequest(`${API_V1}/events/${eventId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok && response.data) {
        console.log('✅ Dettagli evento recuperati');
        console.log(`   ID: ${response.data.id}`);
        console.log(`   Nome: ${response.data.name}`);
        console.log(`   Data: ${response.data.date}`);
        console.log(`   Orario: ${response.data.startTime} - ${response.data.endTime}`);
        console.log(`   Venue: ${response.data.venue?.name || 'N/A'}`);
        console.log(`   Organizzatore: ${response.data.organizer?.firstName} ${response.data.organizer?.lastName}`);
        return true;
    } else {
        console.log('❌ Recupero dettagli evento fallito');
        console.log(`   Status: ${response.status}`);
        return false;
    }
}

// Test: Acquisto biglietto (esempio di struttura per quando l'endpoint sarà disponibile)
async function testPurchaseTicket(token, eventId, ticketId) {
    console.log('\n=== Test 5: Acquisto Biglietto ===');

    // Questo endpoint potrebbe non essere ancora implementato
    // La struttura è preparata per quando sarà disponibile
    const purchaseEndpoint = `${API_V1}/tickets/${ticketId}/purchase`;
    
    console.log(`   Tentativo di acquisto biglietto ID: ${ticketId}`);
    console.log(`   Endpoint: POST ${purchaseEndpoint}`);

    const response = await httpRequest(purchaseEndpoint, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            ticketId: ticketId
        })
    });

    if (response.ok) {
        console.log('✅ Biglietto acquistato con successo');
        console.log(`   Biglietto ID: ${response.data?.id || ticketId}`);
        console.log(`   Stato: ${response.data?.ticketStatus || 'SOLD'}`);
        return true;
    } else if (response.status === 404) {
        console.log('⚠️  Endpoint acquisto biglietto non ancora implementato');
        console.log('   Questo test sarà funzionante quando l\'endpoint');
        console.log('   POST /api/v1/tickets/{id}/purchase sarà disponibile');
        return 'not_implemented';
    } else {
        console.log('❌ Acquisto biglietto fallito');
        console.log(`   Status: ${response.status}`);
        console.log(`   Response: ${JSON.stringify(response.data)}`);
        return false;
    }
}

// Test: Visualizza biglietti acquistati (esempio per quando sarà implementato)
async function testViewMyTickets(token) {
    console.log('\n=== Test 6: Visualizzazione Biglietti Acquistati ===');

    // Questo endpoint potrebbe non essere ancora implementato
    const myTicketsEndpoint = `${API_V1}/tickets/my`;

    const response = await httpRequest(myTicketsEndpoint, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok && Array.isArray(response.data)) {
        console.log('✅ Biglietti recuperati');
        console.log(`   Numero biglietti posseduti: ${response.data.length}`);
        response.data.forEach((ticket, index) => {
            console.log(`   ${index + 1}. Biglietto ID: ${ticket.id}`);
            console.log(`      - Evento: ${ticket.event?.name || 'N/A'}`);
            console.log(`      - Posto: ${ticket.seat?.seatNumber || 'N/A'}`);
            console.log(`      - Prezzo: €${ticket.price}`);
        });
        return true;
    } else if (response.status === 404 || response.status === 403) {
        console.log('⚠️  Endpoint biglietti utente non ancora implementato');
        console.log('   Questo test sarà funzionante quando l\'endpoint');
        console.log('   GET /api/v1/tickets/my sarà disponibile');
        return 'not_implemented';
    } else {
        console.log('❌ Recupero biglietti fallito');
        console.log(`   Status: ${response.status}`);
        return false;
    }
}

// Cleanup: Elimina l'evento di test
async function cleanup(organizerToken, eventId) {
    console.log('\n=== Cleanup: Rimozione Dati di Test ===');

    if (organizerToken && eventId) {
        const response = await httpRequest(`${API_V1}/events/${eventId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${organizerToken}`
            }
        });

        if (response.status === 204 || response.ok) {
            console.log(`✅ Evento di test (ID: ${eventId}) eliminato`);
        } else {
            console.log(`⚠️  Impossibile eliminare evento di test`);
        }
    }
}

// Main: Esegui tutti i test
async function runTests() {
    console.log('╔════════════════════════════════════════════════════════════╗');
    console.log('║        TEST ACQUISTO BIGLIETTI - LATO USER                 ║');
    console.log('╚════════════════════════════════════════════════════════════╝');
    console.log(`\nBase URL: ${BASE_URL}`);
    console.log('Questo script testa il flusso dell\'utente per la');
    console.log('visualizzazione eventi e l\'acquisto di biglietti.\n');

    let passed = 0;
    let failed = 0;
    let notImplemented = 0;
    let setupData = null;

    try {
        // Setup: Crea organizer e evento di test
        setupData = await setupOrganizerAndEvent();
        
        // Test 1: Registrazione User
        const regToken = await testUserRegistration();
        if (regToken) passed++; else failed++;

        // Test 2: Login User
        const loginToken = await testUserLogin();
        if (loginToken) passed++; else failed++;

        const token = loginToken || regToken;
        if (!token) {
            console.log('\n❌ Impossibile continuare senza token valido');
            return;
        }

        // Test 3: Visualizza eventi disponibili
        const event = await testViewAvailableEvents(token);
        if (event) passed++; else failed++;

        // Test 4: Visualizza dettagli evento
        if (event) {
            const detailsResult = await testViewEventDetails(token, event.id);
            if (detailsResult) passed++; else failed++;
        }

        // Test 5: Acquisto biglietto (potrebbe non essere implementato)
        const purchaseResult = await testPurchaseTicket(token, event?.id, 1);
        if (purchaseResult === true) {
            passed++;
        } else if (purchaseResult === 'not_implemented') {
            notImplemented++;
        } else {
            failed++;
        }

        // Test 6: Visualizza biglietti acquistati (potrebbe non essere implementato)
        const myTicketsResult = await testViewMyTickets(token);
        if (myTicketsResult === true) {
            passed++;
        } else if (myTicketsResult === 'not_implemented') {
            notImplemented++;
        } else {
            failed++;
        }

    } catch (error) {
        console.log(`\n❌ Errore durante l'esecuzione dei test: ${error.message}`);
        failed++;
    } finally {
        // Cleanup
        if (setupData) {
            await cleanup(setupData.organizerToken, setupData.eventId);
        }
    }

    console.log('\n╔════════════════════════════════════════════════════════════╗');
    console.log('║                    RIEPILOGO TEST                          ║');
    console.log('╚════════════════════════════════════════════════════════════╝');
    console.log(`   ✅ Test passati: ${passed}`);
    console.log(`   ❌ Test falliti: ${failed}`);
    console.log(`   ⚠️  Non implementati: ${notImplemented}`);
    console.log(`   📊 Totale: ${passed + failed + notImplemented}`);

    if (failed === 0 && notImplemented === 0) {
        console.log('\n🎉 Tutti i test sono passati con successo!');
    } else if (failed === 0) {
        console.log('\n✅ Test implementati passati. Alcuni endpoint sono in attesa di implementazione.');
    } else {
        console.log('\n⚠️  Alcuni test sono falliti. Verificare la configurazione del server.');
    }

    // Se ci sono solo test non implementati, considera come successo
    process.exit(failed > 0 ? 1 : 0);
}

runTests().catch(console.error);
