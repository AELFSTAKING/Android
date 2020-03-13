package io.alf.exchange.util;

import android.text.TextUtils;

import org.consenlabs.tokencore.wallet.Identity;
import org.consenlabs.tokencore.wallet.Wallet;
import org.consenlabs.tokencore.wallet.WalletManager;
import org.consenlabs.tokencore.wallet.model.ChainType;
import org.consenlabs.tokencore.wallet.model.Metadata;
import org.consenlabs.tokencore.wallet.model.Network;

import java.util.List;

import io.kofo.common.KofoUtil;

public class WalletUtils {

    private static final String TAKER_PRE_SIGN_STRING
            = "makerAmount=%s"
            + "&makerChain=%s"
            + "&makerCurrency=%s"
            + "&takerAddress=%s"
            + "&takerAmount=%s"
            + "&takerChain=%s"
            + "&takerCounterChainAddress=%s"
            + "&takerCurrency=%s"
            + "&takerKofoId=%s";

    public static String takerPreSignStr(
            String kofoSecret,
            String makerAmount,
            String makerChain,
            String makerCurrency,
            String takerAddress,
            String takerAmount,
            String takerChain,
            String takerCounterChainAddress,
            String takerCurrency,
            String takerKofoId) {
        String takerPreSignStr = String.format(
                TAKER_PRE_SIGN_STRING,
                makerAmount,
                makerChain,
                makerCurrency,
                takerAddress,
                takerAmount,
                takerChain,
                takerCounterChainAddress,
                takerCurrency,
                takerKofoId);
        return KofoUtil.sign(kofoSecret, takerPreSignStr);
    }

    public static String getPrivateKey(String password) {
        List<Wallet> walletList = getWalletByIMTokenChain(ChainType.ETHEREUM);
        if (walletList != null && walletList.size() > 0) {
            return walletList.get(0).exportPrivateKey(password);
        } else {
            return "";
        }
    }

    public static String getAddress() {
        List<Wallet> walletList = getWalletByIMTokenChain(ChainType.ETHEREUM);
        if (walletList != null && walletList.size() > 0) {
            return walletList.get(0).getAddress();
        } else {
            return "";
        }
    }

    public static Wallet getWallet() {
        List<Wallet> walletList = getWalletByIMTokenChain(ChainType.ETHEREUM);
        if (walletList != null && walletList.size() > 0) {
            return walletList.get(0);
        } else {
            return null;
        }
    }

    public static boolean changePassword(String oldPassword, String newPassword) {
        Identity identity = Identity.getCurrentIdentity();
        if (identity != null) {
            return identity.changePassword(oldPassword, newPassword);
        } else {
            return false;
        }
    }

    public static List<Wallet> getWalletByIMTokenChain(String chainType) {
        return WalletManager.findWallet(chainType);
    }

    public static synchronized Wallet importEthWallet(String priKey, String password) {
        // 1.导入ETH钱包前，先找出已经存在本地的ETH钱包。
        List<Wallet> oldWalletList = getWalletByIMTokenChain(ChainType.ETHEREUM);
        // 2.导入ETH钱包。
        Metadata metadata = new Metadata(ChainType.ETHEREUM, Network.MAINNET, "ETH", "");
        metadata.setSource(Metadata.FROM_PRIVATE);
        Wallet newWallet = WalletManager.importWalletFromPrivateKey(metadata, priKey, password,
                true);
        // 3.删除已经存在本地的ETH钱包。
        if (newWallet != null) {
            if (oldWalletList != null && oldWalletList.size() > 0) {
                for (Wallet wallet : oldWalletList) {
                    if (!TextUtils.equals(wallet.getId(), newWallet.getId())) {
                        WalletManager.removeWallet(wallet.getId(), password);
                    }
                }
            }
        }
        return newWallet;
    }
}
