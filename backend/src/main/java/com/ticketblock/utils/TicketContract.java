package com.ticketblock.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    private static final String BINARY = "608060405234801561001057600080fd5b506040516102f23803806102f283398101604052805101805161003a906001906020840190610041565b50506100ab565b82805482825590600052602060002090810192821561007e579160200282015b8281111561007e5782518255602090920191600190910190610061565b5061008a92915061008e565b5090565b6100a891905b8082111561008a5760008155600101610094565b90565b610238806100ba6000396000f30060806040526004361061006c5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416632f265cf78114610071578063392e66781461009f5780637021939f146100cb578063b13c744b146100e3578063cc9ab2671461010d575b600080fd5b34801561007d57600080fd5b50610089600435610127565b6040805160ff9092168252519081900360200190f35b3480156100ab57600080fd5b506100b7600435610153565b604080519115158252519081900360200190f35b3480156100d757600080fd5b506100896004356101a0565b3480156100ef57600080fd5b506100fb6004356101b5565b60408051918252519081900360200190f35b34801561011957600080fd5b506101256004356101d4565b005b600061013282610153565b151561013d57600080fd5b5060009081526020819052604090205460ff1690565b6000805b60015481101561019557600180548491908390811061017257fe5b600091825260209091200154141561018d576001915061019a565b600101610157565b600091505b50919050565b60006020819052908152604090205460ff1681565b60018054829081106101c357fe5b600091825260209091200154905081565b6101dd81610153565b15156101e857600080fd5b6000908152602081905260409020805460ff8082166001011660ff199091161790555600a165627a7a723058205ed8289d2c1198057008c4eb35608e7db4a4dbc168d3402c21220308d96660b20029";

    protected TicketContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }



    public RemoteCall<TransactionReceipt> mintTicket(String proprietario, BigInteger prezzo, boolean vendibile, String info) {
        // Costruisci la funzione come nel contratto Solidity
        final Function function = new Function(
                "mintTicket", // nome della funzione Solidity
                Arrays.<Type>asList(
                        new org.web3j.abi.datatypes.Address(proprietario),
                        new org.web3j.abi.datatypes.generated.Uint256(prezzo),
                        new org.web3j.abi.datatypes.Bool(vendibile),
                        new org.web3j.abi.datatypes.Utf8String(info)
                ),
                Arrays.<TypeReference<?>>asList()
        );

        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferTicket(String to, BigInteger ticket) {
        // Costruisci la funzione come nel contratto Solidity
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


}
