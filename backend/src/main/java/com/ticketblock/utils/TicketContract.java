package com.ticketblock.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ticketblock.exception.BlockchainException;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;


public class TicketContract extends Contract {

    //bytecode che sta nel json in build
    private static final String BINARY = "608060405234801561001057600080fd5b506001600081905550610ec7806100286000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c806351958a561461005c5780637dc379fa1461007857806394a681be146100ab578063a476b73d146100db578063b49a8c0d146100f7575b600080fd5b610076600480360381019061007191906108c6565b610115565b005b610092600480360381019061008d9190610906565b610232565b6040516100a29493929190610a05565b60405180910390f35b6100c560048036038101906100c09190610bb2565b61026c565b6040516100d29190610c35565b60405180910390f35b6100f560048036038101906100f09190610906565b6103bb565b005b6100ff610427565b60405161010c9190610c35565b60405180910390f35b600061012082610477565b90508273ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603610190576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161018790610cc2565b60405180910390fd5b610199826104ff565b6101d8576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101cf90610d2e565b60405180910390fd5b826001600084815260200190815260200160002060010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505050565b6000806000606061024285610477565b61024b86610574565b610254876104ff565b61025d886105dc565b93509350935093509193509193565b600080600054905060405180608001604052808681526020018773ffffffffffffffffffffffffffffffffffffffff168152602001851515815260200184815250600160008381526020019081526020016000206000820151816000015560208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160010160146101000a81548160ff021916908315150217905550606082015181600201908051906020019061034d92919061073b565b5090505060008081548092919061036390610d7d565b91905055508315158673ffffffffffffffffffffffffffffffffffffffff16827fe8347d77b1309626c60cf39bf781e719006de67224bb9f5424831a14ba13952f60405160405180910390a480915050949350505050565b600160008281526020019081526020016000206000808201600090556001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556001820160146101000a81549060ff021916905560028201600061042291906107c1565b505050565b600080600090506000600190505b60005481101561046f57610448816106cc565b1561045c57818061045890610d7d565b9250505b808061046790610d7d565b915050610435565b508091505090565b6000610482826106cc565b6104c1576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104b890610e11565b60405180910390fd5b6001600083815260200190815260200160002060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b600061050a826106cc565b610549576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161054090610e11565b60405180910390fd5b6001600083815260200190815260200160002060010160149054906101000a900460ff169050919050565b600061057f826106cc565b6105be576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016105b590610e11565b60405180910390fd5b60016000838152602001908152602001600020600001549050919050565b60606105e7826106cc565b610626576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161061d90610e11565b60405180910390fd5b60016000838152602001908152602001600020600201805461064790610e60565b80601f016020809104026020016040519081016040528092919081815260200182805461067390610e60565b80156106c05780601f10610695576101008083540402835291602001916106c0565b820191906000526020600020905b8154815290600101906020018083116106a357829003601f168201915b50505050509050919050565b60008073ffffffffffffffffffffffffffffffffffffffff166001600084815260200190815260200160002060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614159050919050565b82805461074790610e60565b90600052602060002090601f01602090048101928261076957600085556107b0565b82601f1061078257805160ff19168380011785556107b0565b828001600101855582156107b0579182015b828111156107af578251825591602001919060010190610794565b5b5090506107bd9190610801565b5090565b5080546107cd90610e60565b6000825580601f106107df57506107fe565b601f0160209004906000526020600020908101906107fd9190610801565b5b50565b5b8082111561081a576000816000905550600101610802565b5090565b6000604051905090565b600080fd5b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b600061085d82610832565b9050919050565b61086d81610852565b811461087857600080fd5b50565b60008135905061088a81610864565b92915050565b6000819050919050565b6108a381610890565b81146108ae57600080fd5b50565b6000813590506108c08161089a565b92915050565b600080604083850312156108dd576108dc610828565b5b60006108eb8582860161087b565b92505060206108fc858286016108b1565b9150509250929050565b60006020828403121561091c5761091b610828565b5b600061092a848285016108b1565b91505092915050565b61093c81610852565b82525050565b61094b81610890565b82525050565b60008115159050919050565b61096681610951565b82525050565b600081519050919050565b600082825260208201905092915050565b60005b838110156109a657808201518184015260208101905061098b565b838111156109b5576000848401525b50505050565b6000601f19601f8301169050919050565b60006109d78261096c565b6109e18185610977565b93506109f1818560208601610988565b6109fa816109bb565b840191505092915050565b6000608082019050610a1a6000830187610933565b610a276020830186610942565b610a34604083018561095d565b8181036060830152610a4681846109cc565b905095945050505050565b610a5a81610951565b8114610a6557600080fd5b50565b600081359050610a7781610a51565b92915050565b600080fd5b600080fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610abf826109bb565b810181811067ffffffffffffffff82111715610ade57610add610a87565b5b80604052505050565b6000610af161081e565b9050610afd8282610ab6565b919050565b600067ffffffffffffffff821115610b1d57610b1c610a87565b5b610b26826109bb565b9050602081019050919050565b82818337600083830152505050565b6000610b55610b5084610b02565b610ae7565b905082815260208101848484011115610b7157610b70610a82565b5b610b7c848285610b33565b509392505050565b600082601f830112610b9957610b98610a7d565b5b8135610ba9848260208601610b42565b91505092915050565b60008060008060808587031215610bcc57610bcb610828565b5b6000610bda8782880161087b565b9450506020610beb878288016108b1565b9350506040610bfc87828801610a68565b925050606085013567ffffffffffffffff811115610c1d57610c1c61082d565b5b610c2987828801610b84565b91505092959194509250565b6000602082019050610c4a6000830184610942565b92915050565b7f496c2064657374696e61746172696f206527206c2761747475616c65206f776e60008201527f65722064656c207469636b657400000000000000000000000000000000000000602082015250565b6000610cac602d83610977565b9150610cb782610c50565b604082019050919050565b60006020820190508181036000830152610cdb81610c9f565b9050919050565b7f4269676c696574746f206e6f6e20726573656c6c61626c650000000000000000600082015250565b6000610d18601883610977565b9150610d2382610ce2565b602082019050919050565b60006020820190508181036000830152610d4781610d0b565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000610d8882610890565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8203610dba57610db9610d4e565b5b600182019050919050565b7f4269676c696574746f206e6f6e206573697374656e7465000000000000000000600082015250565b6000610dfb601783610977565b9150610e0682610dc5565b602082019050919050565b60006020820190508181036000830152610e2a81610dee565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60006002820490506001821680610e7857607f821691505b602082108103610e8b57610e8a610e31565b5b5091905056fea26469706673582212206b7f5785ee425032e700c4481b95203fe7f0275a23971a914496afbef2b5d26f64736f6c634300080d0033";

    protected TicketContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }



    public RemoteCall<TransactionReceipt> mintTicket(String proprietario, BigInteger prezzo, boolean vendibile, String info) {
        final Function function = new Function(
                "mintTicket", // nome della funzione Solidity
                Arrays.<Type>asList(
                        new org.web3j.abi.datatypes.Address(proprietario),
                        new org.web3j.abi.datatypes.generated.Uint256(prezzo),
                        new org.web3j.abi.datatypes.Bool(vendibile),
                        new org.web3j.abi.datatypes.Utf8String(info)
                ),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {})
        );
        System.out.println("HA MINTATOOO");

        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferTicket(String to, BigInteger ticket) {
        final Function function = new Function(
                "transferTicket", // nome della funzione Solidity
                Arrays.<Type>asList(
                        new org.web3j.abi.datatypes.Address(to),
                        new org.web3j.abi.datatypes.generated.Uint256(ticket)
                ),
                Arrays.<TypeReference<?>>asList()
        );

        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burnTicket(BigInteger ticket) {
        // Costruisci la funzione come nel contratto Solidity
        final Function function = new Function(
                "burnTicket", // nome della funzione Solidity
                Arrays.<Type>asList(
                        new org.web3j.abi.datatypes.generated.Uint256(ticket)
                ),
                Arrays.<TypeReference<?>>asList()
        );

        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totaleBiglietti() {
        final Function function = new Function(
                "totaleBiglietti",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {})
        );

        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<List<Type>> getTicket(BigInteger ticket) {
        final Function function = new Function(
                "getTicket",
                Arrays.<Type>asList(
                        new org.web3j.abi.datatypes.generated.Uint256(ticket)
                ),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>() {},
                        new TypeReference<Uint256>() {},
                        new TypeReference<Bool>() {},
                        new TypeReference<Utf8String>() {}
                )
        );

        return executeRemoteCallMultipleValueReturn(function);
    }

    /**
     * Verifica se un biglietto è resellable sulla blockchain
     * @param ticketId ID del biglietto sulla blockchain
     * @return true se il biglietto è resellable, false altrimenti
     */
    public boolean isTicketResellable(BigInteger ticketId) {
        try {
            List<Type> result = getTicket(ticketId).send();
            // Il terzo elemento (indice 2) è resellable (bool)
            return (Boolean) result.get(2).getValue();
        } catch (Exception e) {
            throw new BlockchainException("Error retrieving ticket's resellable status from blockchain");
        }
    }

    /**
     * Verifica l'ownership di un biglietto sulla blockchain
     * @param ticketId ID del biglietto sulla blockchain
     * @param ownerAddress Indirizzo del wallet del proprietario atteso
     * @return true se il proprietario corrisponde, false altrimenti
     */
    public boolean verifyTicketOwnership(BigInteger ticketId, String ownerAddress) {
        try {
            List<Type> result = getTicket(ticketId).send();
            // Il primo elemento (indice 0) è l'owner (address)
            String blockchainOwner = (String) result.get(0).getValue();
            return blockchainOwner.equalsIgnoreCase(ownerAddress);
        } catch (Exception e) {
            throw new BlockchainException("Error verifying ticket ownership on the blockchain");
        }
    }


    //serve per deployare il contratto se vuoi farlo da qua, lo devi fare dopo aver fatto truffle compile che genera il json del contratto
    //ma non serve tanto basta fare truffle migrate o truffle migrate -reset
    public static RemoteCall<TicketContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, List<byte[]> candidateNames) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                org.web3j.abi.Utils.typeMap(candidateNames, org.web3j.abi.datatypes.generated.Bytes32.class))));
        return deployRemoteCall(TicketContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }



    public static TicketContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TicketContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static final Event TICKETMINTED_EVENT = new Event("TicketMinted",
            Arrays.<TypeReference<?>>asList(
                    new TypeReference<Uint256>(true) {},      // ticketID (indexed)
                    new TypeReference<Address>(true) {},     // owner (indexed)
                    new TypeReference<Bool>(true) {}         // resellable (indexed)
            )
    );

    public BigInteger getTicketIdFromReceipt(TransactionReceipt transactionReceipt) {
        // Estrae i parametri dell'evento dai log della ricevuta
        List<Contract.EventValuesWithLog> values = extractEventParametersWithLog(TICKETMINTED_EVENT, transactionReceipt);

        if (values != null && !values.isEmpty()) {
            // Poiché ticketID è il primo parametro 'indexed', lo troviamo negli IndexedValues
            // Il valore è di tipo Uint256, quindi restituiamo il suo BigInteger
            return (BigInteger) values.get(0).getIndexedValues().get(0).getValue();
        }
        return null;
    }


}
