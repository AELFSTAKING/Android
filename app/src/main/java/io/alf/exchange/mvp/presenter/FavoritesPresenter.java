package io.alf.exchange.mvp.presenter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.alf.exchange.App;
import io.alf.exchange.mvp.bean.FavoriteDBSymbol;
import io.alf.exchange.mvp.bean.FavoriteDBSymbol_;
import io.alf.exchange.mvp.bean.Favorites;
import io.alf.exchange.mvp.view.FavoritesView;
import io.objectbox.Box;

public class FavoritesPresenter extends BasePresenter<FavoritesView> {

    private Box<FavoriteDBSymbol> mBox;

    public FavoritesPresenter() {
        super();
        mBox = App.getBoxStore().boxFor(FavoriteDBSymbol.class);
    }

    public void queryFavorites() {
        queryFavoritesFromLocal();
    }

    private void queryFavoritesFromLocal() {
        List<FavoriteDBSymbol> favoriteDBSymbols = mBox.query().build().find();
        List<String> symbols = new ArrayList<>();
        for (FavoriteDBSymbol favoriteDBSymbol : favoriteDBSymbols) {
            symbols.add(favoriteDBSymbol.getSymbol());
        }
        Favorites favorites = new Favorites(symbols);
        if (getView() != null) {
            getView().onQueryFavorites(favorites);
        }
    }


    public void addFavorite(String symbol) {
        Log.i("Dupeng", "addFavorite : " + symbol);
        addFavoriteToLocal(symbol);
    }

    private void addFavoriteToLocal(String symbol) {
        FavoriteDBSymbol favoriteDBSymbol = new FavoriteDBSymbol();
        favoriteDBSymbol.setSymbol(symbol);
        long row = mBox.put(favoriteDBSymbol);
        getView().onAddFavorite(symbol, row != -1);
    }

    public void deleteFavorite(String symbol) {
        Log.i("Dupeng", "deleteFavorite : " + symbol);
        deleteFavoriteFromLocal(symbol);
    }

    private void deleteFavoriteFromLocal(String symbol) {
        //ObjectBox 不支持传入 null 作为参数
        if (symbol == null) {
            return;
        }
        List<FavoriteDBSymbol> favoriteDBSymbols = mBox.query()
                .equal(FavoriteDBSymbol_.symbol, symbol)
                .build()
                .find();
        if (favoriteDBSymbols.isEmpty()) {
            return;
        }
        mBox.remove(favoriteDBSymbols.get(0).getId());
        getView().onDeleteFavorite(symbol, true);
    }
}
