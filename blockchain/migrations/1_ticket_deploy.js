const EventTicker = artifacts.require("EventTicket");

module.exports = function (deployer) {
    deployer.deploy(EventTicker);
};