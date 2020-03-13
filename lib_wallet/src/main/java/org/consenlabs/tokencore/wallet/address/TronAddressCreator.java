package org.consenlabs.tokencore.wallet.address;

import org.tron.generate.KeyGenerator;
import org.tron.generate.NetType;

/**
 * Tick.Du: Add the class
 */
public class TronAddressCreator implements AddressCreator {

    private NetType netType;

    public TronAddressCreator(NetType netType) {
        this.netType = netType;
    }

    @Override
    public String fromPrivateKey(String prvKeyHex) {
        return KeyGenerator.generate(netType, prvKeyHex).getAddress();
    }

    @Override
    public String fromPrivateKey(byte[] prvKeyBytes) {
        return KeyGenerator.generate(netType, prvKeyBytes).getAddress();
    }

    @Override
    public String pubKeyFromPrivateKey(String prvKeyHex) {
        return null;
    }

    @Override
    public String pubKeyFromPrivateKey(byte[] prvKeyBytes) {
        return null;
    }
}
