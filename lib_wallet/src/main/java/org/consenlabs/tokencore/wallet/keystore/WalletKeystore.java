package org.consenlabs.tokencore.wallet.keystore;

public abstract class WalletKeystore extends Keystore {
    String address;
    String pubKey;

    public WalletKeystore() {
        super();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }
}
