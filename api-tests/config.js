// Configuration for API tests
const config = {
  // Base URL for the API - adjust this to match your backend server
  baseURL: process.env.API_BASE_URL || 'http://localhost:8080/api/v1',
  
  // Timeout for API requests (in milliseconds)
  timeout: 10000,
  
  // Test user credentials
  testUsers: {
    organizer: {
      firstName: 'Test',
      lastName: 'Organizer',
      email: 'organizer@test.com',
      password: 'Test1234!',
      role: 'ORGANIZER'
    },
    user: {
      firstName: 'Test',
      lastName: 'User',
      email: 'user@test.com',
      password: 'Test1234!',
      role: 'USER'
    }
  },
  
  // Test credit card for purchases
  testCreditCard: {
    creditCardNumber: '4532015112830366', // Valid Visa test card
    expirationDate: '12/25',
    cvv: '123',
    cardHolderName: 'Test User'
  }
};

module.exports = config;
