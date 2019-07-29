package top.easelink.lcg.ui.main.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

final public class ForumNavigationModel {

    private String mTitle;
    @DrawableRes
    private int mDrawableRes;
    private String mUrl;

    public ForumNavigationModel(@NonNull String title, int drawableRes, String url) {
        mTitle = title;
        mDrawableRes = drawableRes;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getDrawableRes() {
        return mDrawableRes;
    }

    public String getUrl() {
        return mUrl;
    }
}