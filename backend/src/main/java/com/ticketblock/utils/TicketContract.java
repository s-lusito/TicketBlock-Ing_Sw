package com.ticketblock.utils;

import com.ticketblock.exception.BlockchainException;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;


public class TicketContract extends Contract {

    //bytecode che sta nel json in build
    private static final String BINARY = "608060405234801561001057600080fd5b506001600081905550610eac806100286000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c806351958a561461005c5780637dc379fa1461007857806394a681be146100ab578063a476b73d146100c7578063b49a8c0d146100e3575b600080fd5b610076600480360381019061007191906108ab565b610101565b005b610092600480360381019061008d91906108eb565b61021e565b6040516100a294939291906109ea565b60405180910390f35b6100c560048036038101906100c09190610b97565b610258565b005b6100e160048036038101906100dc91906108eb565b6103a0565b005b6100eb61040c565b6040516100f89190610c1a565b60405180910390f35b600061010c8261045c565b90508273ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff160361017c576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161017390610ca7565b60405180910390fd5b610185826104e4565b6101c4576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101bb90610d13565b60405180910390fd5b826001600084815260200190815260200160002060010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505050565b6000806000606061022e8561045c565b61023786610559565b610240876104e4565b610249886105c1565b93509350935093509193509193565b60008054905060405180608001604052808581526020018673ffffffffffffffffffffffffffffffffffffffff168152602001841515815260200183815250600160008381526020019081526020016000206000820151816000015560208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160010160146101000a81548160ff0219169083151502179055506060820151816002019080519060200190610337929190610720565b5090505060008081548092919061034d90610d62565b91905055508215158573ffffffffffffffffffffffffffffffffffffffff16827fe8347d77b1309626c60cf39bf781e719006de67224bb9f5424831a14ba13952f60405160405180910390a45050505050565b600160008281526020019081526020016000206000808201600090556001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556001820160146101000a81549060ff021916905560028201600061040791906107a6565b505050565b600080600090506000600190505b6000548110156104545761042d816106b1565b1561044157818061043d90610d62565b9250505b808061044c90610d62565b91505061041a565b508091505090565b6000610467826106b1565b6104a6576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161049d90610df6565b60405180910390fd5b6001600083815260200190815260200160002060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b60006104ef826106b1565b61052e576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161052590610df6565b60405180910390fd5b6001600083815260200190815260200160002060010160149054906101000a900460ff169050919050565b6000610564826106b1565b6105a3576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161059a90610df6565b60405180910390fd5b60016000838152602001908152602001600020600001549050919050565b60606105cc826106b1565b61060b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161060290610df6565b60405180910390fd5b60016000838152602001908152602001600020600201805461062c90610e45565b80601f016020809104026020016040519081016040528092919081815260200182805461065890610e45565b80156106a55780601f1061067a576101008083540402835291602001916106a5565b820191906000526020600020905b81548152906001019060200180831161068857829003601f168201915b50505050509050919050565b60008073ffffffffffffffffffffffffffffffffffffffff166001600084815260200190815260200160002060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614159050919050565b82805461072c90610e45565b90600052602060002090601f01602090048101928261074e5760008555610795565b82601f1061076757805160ff1916838001178555610795565b82800160010185558215610795579182015b82811115610794578251825591602001919060010190610779565b5b5090506107a291906107e6565b5090565b5080546107b290610e45565b6000825580601f106107c457506107e3565b601f0160209004906000526020600020908101906107e291906107e6565b5b50565b5b808211156107ff5760008160009055506001016107e7565b5090565b6000604051905090565b600080fd5b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b600061084282610817565b9050919050565b61085281610837565b811461085d57600080fd5b50565b60008135905061086f81610849565b92915050565b6000819050919050565b61088881610875565b811461089357600080fd5b50565b6000813590506108a58161087f565b92915050565b600080604083850312156108c2576108c161080d565b5b60006108d085828601610860565b92505060206108e185828601610896565b9150509250929050565b6000602082840312156109015761090061080d565b5b600061090f84828501610896565b91505092915050565b61092181610837565b82525050565b61093081610875565b82525050565b60008115159050919050565b61094b81610936565b82525050565b600081519050919050565b600082825260208201905092915050565b60005b8381101561098b578082015181840152602081019050610970565b8381111561099a576000848401525b50505050565b6000601f19601f8301169050919050565b60006109bc82610951565b6109c6818561095c565b93506109d681856020860161096d565b6109df816109a0565b840191505092915050565b60006080820190506109ff6000830187610918565b610a0c6020830186610927565b610a196040830185610942565b8181036060830152610a2b81846109b1565b905095945050505050565b610a3f81610936565b8114610a4a57600080fd5b50565b600081359050610a5c81610a36565b92915050565b600080fd5b600080fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610aa4826109a0565b810181811067ffffffffffffffff82111715610ac357610ac2610a6c565b5b80604052505050565b6000610ad6610803565b9050610ae28282610a9b565b919050565b600067ffffffffffffffff821115610b0257610b01610a6c565b5b610b0b826109a0565b9050602081019050919050565b82818337600083830152505050565b6000610b3a610b3584610ae7565b610acc565b905082815260208101848484011115610b5657610b55610a67565b5b610b61848285610b18565b509392505050565b600082601f830112610b7e57610b7d610a62565b5b8135610b8e848260208601610b27565b91505092915050565b60008060008060808587031215610bb157610bb061080d565b5b6000610bbf87828801610860565b9450506020610bd087828801610896565b9350506040610be187828801610a4d565b925050606085013567ffffffffffffffff811115610c0257610c01610812565b5b610c0e87828801610b69565b91505092959194509250565b6000602082019050610c2f6000830184610927565b92915050565b7f496c2064657374696e61746172696f206527206c2761747475616c65206f776e60008201527f65722064656c207469636b657400000000000000000000000000000000000000602082015250565b6000610c91602d8361095c565b9150610c9c82610c35565b604082019050919050565b60006020820190508181036000830152610cc081610c84565b9050919050565b7f4269676c696574746f206e6f6e20726573656c6c61626c650000000000000000600082015250565b6000610cfd60188361095c565b9150610d0882610cc7565b602082019050919050565b60006020820190508181036000830152610d2c81610cf0565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000610d6d82610875565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8203610d9f57610d9e610d33565b5b600182019050919050565b7f4269676c696574746f206e6f6e206573697374656e7465000000000000000000600082015250565b6000610de060178361095c565b9150610deb82610daa565b602082019050919050565b60006020820190508181036000830152610e0f81610dd3565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60006002820490506001821680610e5d57607f821691505b602082108103610e7057610e6f610e16565b5b5091905056fea2646970667358221220f54b44b2924e493837d504f4221a79fea3da01400f36af5e67dea4fd931c139c64736f6c634300080d0033";

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
        System.out.println("bruciaaaa");
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
