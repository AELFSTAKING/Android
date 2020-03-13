package org.consenlabs.tokencore.wallet.model;

public class ChainType {
    public final static String ETHEREUM = "ETHEREUM";
    public final static String BITCOIN = "BITCOIN";
    public final static String EOS = "EOS";
    // Tick.Du: Add TRON
    public final static String TRON = "TRON";
    // Tick.Du: Add ZIL
    public final static String ZIL = "ZILLIQA";
    // Tick.Du: Add HPB
    public final static String HPB = "HPB";


    public static void validate(String type) {
        // Tick.Du: Add TRON, ZILL and HPB's support.
        if (!ETHEREUM.equals(type)
                && !BITCOIN.equals(type)
                && !EOS.equals(type)
                && !TRON.equals(type)
                && !ZIL.equals(type)
                && !HPB.equals(type)
        ) {
            throw new TokenException(Messages.WALLET_INVALID_TYPE);
        }
    }
}
