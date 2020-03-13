package io.alf.exchange.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaojianjun on 2018/8/7.
 */
public class FavoritesCache {

    private static volatile FavoritesCache sInstance;
    private List<String> mSymbols;
    private boolean isCached;

    private FavoritesCache() {
        mSymbols = new ArrayList<>();
    }

    public static FavoritesCache instance() {
        if (sInstance == null) {
            synchronized (FavoritesCache.class) {
                if (sInstance == null) {
                    sInstance = new FavoritesCache();
                }
            }
        }
        return sInstance;
    }

    public void put(List<String> symbols) {
        isCached = true;
        mSymbols.clear();
        if (symbols != null) {
            mSymbols.addAll(symbols);
        }
    }

    public void add(String symbol) {
        if (isCached && !mSymbols.contains(symbol)) {
            mSymbols.add(symbol);
        }
    }

    public void add(int index, String symbol) {
        if (isCached && !mSymbols.contains(symbol)) {
            mSymbols.add(index, symbol);
        }
    }

    public int delete(String symbol) {
        if (isCached && mSymbols.contains(symbol)) {
            int index = mSymbols.indexOf(symbol);
            mSymbols.remove(symbol);
            return index;
        }
        return -1;
    }

    public List<String> get() {
        return isCached ? new ArrayList<>(mSymbols) : null;
    }
}
