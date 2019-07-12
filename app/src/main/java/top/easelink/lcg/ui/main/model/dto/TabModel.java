package top.easelink.lcg.ui.main.model.dto;

import androidx.annotation.NonNull;

final public class TabModel {

    private String mTitle;
    private String mUrlParam;

    public TabModel(@NonNull String title, String param) {
        mTitle = title;
        mUrlParam = param;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrlParam;
    }
}