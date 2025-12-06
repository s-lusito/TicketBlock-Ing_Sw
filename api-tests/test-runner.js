const { printSummary } = require('./test-utils');

// Import all test suites
const runAuthTests = require('./tests/auth.test');
const runEventTests = require('./tests/events.test');
const runVenueTests = require('./tests/venues.test');
const runTicketTests = require('./tests/tickets.test');
const runEventTicketsTests = require('./tests/event-tickets.test');

async function runAllTests() {
  console.log('\n');
  console.log('╔════════════════════════════════════════════════════════════╗');
  console.log('║        TicketBlock API Test Suite                         ║');
  console.log('║        Testing all API endpoints                          ║');
  console.log('╚════════════════════════════════════════════════════════════╝');
  console.log('\n');

  try {
    // Run all test suites
    await runAuthTests();
    await runEventTests();
    await runVenueTests();
    await runTicketTests();
    await runEventTicketsTests();

    // Print summary
    printSummary();
  } catch (error) {
    console.error('\n❌ Error running tests:', error.message);
    console.error(error.stack);
    process.exit(1);
  }
}

// Run all tests
runAllTests();
