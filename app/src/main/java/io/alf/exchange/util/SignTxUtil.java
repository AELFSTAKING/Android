package io.alf.exchange.util;

import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

public class SignTxUtil {
    public static String signTx(String rawTransaction, String password) {
        String signedRawTransaction = null;
        try {
            String priKey = WalletUtils.getPrivateKey(password);
            Credentials credentials = Credentials.create(priKey);
            RawTransaction decodedRawTransaction = TransactionDecoder.decode(
                    rawTransaction);
            signedRawTransaction = Numeric.toHexString(
                    TransactionEncoder.signMessage(decodedRawTransaction, credentials));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Tick", "Eth signedRawTransaction : " + signedRawTransaction);
        return signedRawTransaction;
    }
}
