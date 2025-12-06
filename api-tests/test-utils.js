const axios = require('axios');
const config = require('./config');

// Create axios instance with base configuration
const api = axios.create({
  baseURL: config.baseURL,
  timeout: config.timeout,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add request interceptor to include auth token if available
api.interceptors.request.use(
  (config) => {
    const token = global.authToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Test result tracking
let totalTests = 0;
let passedTests = 0;
let failedTests = 0;

class TestRunner {
  constructor(suiteName) {
    this.suiteName = suiteName;
    this.tests = [];
  }

  // Add a test case
  test(description, testFn) {
    this.tests.push({ description, testFn });
  }

  // Run all tests in the suite
  async run() {
    console.log(`\n${'='.repeat(60)}`);
    console.log(`📋 Test Suite: ${this.suiteName}`);
    console.log('='.repeat(60));

    for (const { description, testFn } of this.tests) {
      totalTests++;
      try {
        await testFn();
        passedTests++;
        console.log(`✅ PASS: ${description}`);
      } catch (error) {
        failedTests++;
        console.log(`❌ FAIL: ${description}`);
        console.log(`   Error: ${error.message}`);
        if (error.response) {
          console.log(`   Status: ${error.response.status}`);
          console.log(`   Data: ${JSON.stringify(error.response.data, null, 2)}`);
        }
      }
    }
  }
}

// Assertion helpers
const assert = {
  equal: (actual, expected, message) => {
    if (actual !== expected) {
      throw new Error(message || `Expected ${expected} but got ${actual}`);
    }
  },
  
  notEqual: (actual, expected, message) => {
    if (actual === expected) {
      throw new Error(message || `Expected value not to be ${expected}`);
    }
  },
  
  truthy: (value, message) => {
    if (!value) {
      throw new Error(message || `Expected value to be truthy but got ${value}`);
    }
  },
  
  falsy: (value, message) => {
    if (value) {
      throw new Error(message || `Expected value to be falsy but got ${value}`);
    }
  },
  
  exists: (value, message) => {
    if (value === null || value === undefined) {
      throw new Error(message || 'Expected value to exist');
    }
  },
  
  arrayContains: (array, item, message) => {
    if (!array.includes(item)) {
      throw new Error(message || `Expected array to contain ${item}`);
    }
  },
  
  throws: async (fn, message) => {
    try {
      await fn();
      throw new Error(message || 'Expected function to throw an error');
    } catch (error) {
      if (error.message.includes('Expected function to throw')) {
        throw error;
      }
      // Error was thrown as expected
    }
  }
};

// Print summary
function printSummary() {
  console.log(`\n${'='.repeat(60)}`);
  console.log('📊 Test Summary');
  console.log('='.repeat(60));
  console.log(`Total Tests: ${totalTests}`);
  console.log(`✅ Passed: ${passedTests}`);
  console.log(`❌ Failed: ${failedTests}`);
  console.log(`Success Rate: ${totalTests > 0 ? ((passedTests / totalTests) * 100).toFixed(2) : 0}%`);
  console.log('='.repeat(60));
}

module.exports = {
  api,
  TestRunner,
  assert,
  printSummary,
  config
};
