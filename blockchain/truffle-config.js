
module.exports = {
  // Compilers
  compilers: {
    solc: {
      version: "0.8.13"
    },
  },

  networks: {
    development: {
      host: "127.0.0.1",     // Localhost (Ganache)
      port: 7545,            // Port di Ganache GUI
      network_id: "*",       // Match any network id
    }
  },
  
};
