package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.Favorites;
import io.tick.base.mvp.IView;

public interface FavoritesView extends IView {
    void onAddFavorite(String symbol, boolean success);

    void onDeleteFavorite(String symbol, boolean success);

    void onQueryFavorites(Favorites data);
}
