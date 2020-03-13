package io.tick.base.mvp;

import android.content.res.Configuration;
import android.view.Gravity;
import android.view.ViewGroup;

import io.tick.base.R;


public abstract class BottomDialogFragment extends MvpDialogFragment {

    protected abstract int getHeight(int screenHeight);

    @Override
    public void onStart() {
        super.onStart();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setWindowAnimations(R.style.BottomDialog);
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, getHeight(mHeight));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, getHeight(mHeight));
    }
}
