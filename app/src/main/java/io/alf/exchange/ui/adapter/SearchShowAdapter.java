package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.bean.DBSearchEntity;
import io.alf.exchange.mvp.bean.Favorites;


public class SearchShowAdapter extends BaseQuickAdapter<DBSearchEntity, BaseViewHolder> {

    private Favorites mFavorites;

    private OnItemClickListener listener;

    public SearchShowAdapter() {
        super(R.layout.item_search_db_result);
    }

    @Override
    protected void convert(BaseViewHolder helper, DBSearchEntity item) {
        helper.setText(R.id.tv_trade, item.getSymbol());
        if (mFavorites != null && mFavorites.getSymbolList() != null && mFavorites.getSymbolList().contains(item.getSymbol())) {
            helper.setImageResource(R.id.iv_favorite, R.mipmap.ic_favorite_selected);
        } else {
            helper.setImageResource(R.id.iv_favorite, R.mipmap.ic_favorite_normal);
        }
        helper.addOnClickListener(R.id.iv_favorite);
        helper.getView(R.id.cs_parent).setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClickListener(item);
            }
        });
    }

    public void setFavorites(Favorites data) {
        mFavorites = data;
    }


    public Favorites getFavorites() {
        return mFavorites;
    }

    public interface OnItemClickListener {
        void onItemClickListener(DBSearchEntity item);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
