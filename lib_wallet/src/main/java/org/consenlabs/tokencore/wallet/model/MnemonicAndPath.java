package org.consenlabs.tokencore.wallet.model;

public class MnemonicAndPath {
    private final String mnemonic;
    private final String path;

    public MnemonicAndPath(String mnemonic, String path) {
        this.path = path;
        this.mnemonic = mnemonic;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getPath() {
        return path;
    }
}
