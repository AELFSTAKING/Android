package org.consenlabs.tokencore.wallet.address;

import com.firestack.laksaj.crypto.KeyTools;
import com.firestack.laksaj.utils.ByteUtil;

/**
 * Tick.Du: Add the class
 */
public class ZiIliqaAddressCreator implements AddressCreator {

    public static void main(String args[]) {
        System.out.println(KeyTools.getPublicKeyFromPrivateKey(
                "c03ac6c1b1918ac8ba50285ed1edde1ddcb51ec0ecdf699a4a2f56c19f5961d7", true));
        System.out.println(KeyTools.getPublicKeyFromPrivateKey(
                "c03ac6c1b1918ac8ba50285ed1edde1ddcb51ec0ecdf699a4a2f56c19f5961d7", false));
    }

    @Override
    public String fromPrivateKey(String prvKeyHex) {
        return KeyTools.getAddressFromPrivateKey(prvKeyHex);
    }

    @Override
    public String fromPrivateKey(byte[] prvKeyBytes) {
        return KeyTools.getAddressFromPrivateKey(
                ByteUtil.byteArrayToHexString(prvKeyBytes)).toLowerCase();
    }

    @Override
    public String pubKeyFromPrivateKey(String prvKeyHex) {
        return KeyTools.getPublicKeyFromPrivateKey(prvKeyHex, true);
    }

    @Override
    public String pubKeyFromPrivateKey(byte[] prvKeyBytes) {
        return KeyTools.getPublicKeyFromPrivateKey(ByteUtil.byteArrayToHexString(prvKeyBytes),
                true);
    }
}
