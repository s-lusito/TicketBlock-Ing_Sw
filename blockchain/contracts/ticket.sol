// SPDX-License-Identifier: MIT
pragma solidity ^0.8.13;

//import "@openzeppelin/contracts/token/ERC721/ERC721.sol";

//contract EventTicket is ERC721{

contract EventTicket {
    //string public baseURI; // es. "http://metadata/" => tokenURI(1) = "http://metadata/1"
    event TicketMinted(uint256 indexed ticketID, address indexed proprietario, bool indexed vendibile);

    struct Ticket {
        uint256 prezzo;      // in centesimi
        address proprietario;
        bool vendibile;
        string info;
    }

    uint256 private nextTicketID;
    mapping(uint256 => Ticket) private tickets;

    //constructor() ERC721("MyEvent", "MTK")
    constructor()
    {
        //   baseURI = "http://metadata/";
        nextTicketID = 1;
    }


    // Creazione di un nuovo ticket (solo owner)
    function mintTicket(address _proprietario,uint256 _prezzo,bool _vendibile,string memory _info) external {
        uint256 ticketID = nextTicketID;

        tickets[ticketID] = Ticket({
            prezzo: _prezzo,
            proprietario: _proprietario,
            vendibile: _vendibile,
            info: _info
        });

        // _safeMint(_proprietario, ticketID);
        nextTicketID++;
        emit TicketMinted(ticketID, _proprietario,  _vendibile);

    }

    // Controlla se il biglietto è vendibile
    function isSalable(uint256 ticketID) internal view returns (bool) {
        require(ticketExists(ticketID), "Biglietto non esistente");
        return tickets[ticketID].vendibile;
    }

    function ticketPrice(uint256 ticketID) internal view returns (uint256) {
        require(ticketExists(ticketID), "Biglietto non esistente");
        return tickets[ticketID].prezzo;
    }

    // Ritorna il proprietario del biglietto
    function ticketOwner(uint256 ticketID) internal view returns (address) {
        require(ticketExists(ticketID), "Biglietto non esistente");
        return tickets[ticketID].proprietario;
    }

    // Ritorna le informazioni del biglietto
    function ticketInfo(uint256 ticketID) internal view returns (string memory) {
        require(ticketExists(ticketID), "Biglietto non esistente");
        return tickets[ticketID].info;
    }

    function getTicket(uint256 ticketID) external view returns(address owner,uint256, bool, string memory ){
        return (ticketOwner(ticketID),ticketPrice(ticketID),isSalable(ticketID),ticketInfo(ticketID));
    }

    function totaleBiglietti() external view returns(uint256){
        uint256 count = 0;
        for (uint256 i = 1; i < nextTicketID; i++) {
            if (ticketExists(i)) {
                count++;
            }
        }
        return count;
    }

    // Trasferisce il biglietto da un proprietario a un altro
    function transferTicket(address to,uint256 ticketID) external {
        address owner = ticketOwner(ticketID);
        require(owner != to, "Il destinatario e' l'attuale proprietario del ticket");
        require(isSalable(ticketID), "Biglietto non vendibile");

        // require(msg.sender == owner, "Non sei il proprietario");

        tickets[ticketID].proprietario = to;
        // _safeTransfer(owner, to, ticketID, "");
    }

    // Brucia un biglietto (solo proprietario)
    function burnTicket(uint256 ticketID) external {
        //address owner = ticketOwner(ticketID);
        //require(msg.sender == owner, "Non sei il proprietario");

        delete tickets[ticketID]; //la chiave esiste ancora ma viene inizializzato tutto a 0
        // _burn(ticketID);
    }

    // Funzione di supporto per controllare se un biglietto esiste
    function ticketExists(uint256 ticketID) internal view returns (bool) {
        return tickets[ticketID].proprietario != address(0);
    }

    // Override baseURI di ERC721
    /* function _baseURI() internal view override returns (string memory) {
         return baseURI;
     }*/
}