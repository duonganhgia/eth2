package org.web3j.sample.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.sample.utils.Utils;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class OneETHGas implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(OneETHGas.class);

    @Override
    public void run() {
        Logger log = LoggerFactory.getLogger(OneETHGas.class);
        BigInteger PRIVATE_KEY = Numeric.toBigInt(Utils.PRIVATE_KEY_1);
        Web3j web3j = Web3j.build(new HttpService(Utils.API));  // FIXME: Enter your Infura token here;
        try {
            log.info("Connected to Ethereum client version: "
                    + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ECKeyPair ecKeyPair= new ECKeyPair(PRIVATE_KEY, Sign.publicKeyFromPrivate(PRIVATE_KEY));
        // We then need to load our Ethereum wallet file
        Credentials credentials = Credentials.create(Utils.PRIVATE_KEY_1);
        log.info("Credentials loaded");
        while (true){
            log.info("Sending 0.001 ETH ");
            send(web3j,credentials);

        }
    }

    private void send (Web3j web3j, Credentials credentials){
        try {
            BigDecimal balance = new BigDecimal(web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance()) ;
            log.info("Balance: "+balance);
            if (balance.compareTo(Convert.toWei("0.001", Convert.Unit.ETHER)) ==1){
                TransactionManager tx = new RawTransactionManager(web3j,credentials,(byte)1);
                Transfer transfer = new Transfer(web3j,tx);

                TransactionReceipt transferReceipt = transfer.sendFunds(
                        Utils.ADDRESS_ME,  // you can put any address here
                        BigDecimal.valueOf(0.001), Convert.Unit.ETHER,Convert.toWei("62", Convert.Unit.GWEI).toBigInteger(),Convert.toWei("21000", Convert.Unit.WEI).toBigInteger())  // 1 wei = 10^-18 Ether
                        .send();
                log.info("Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
                        + transferReceipt.getTransactionHash());
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
}
