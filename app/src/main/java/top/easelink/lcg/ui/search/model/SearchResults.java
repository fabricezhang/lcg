package top.easelink.lcg.ui.search.model;

import java.util.List;

public class SearchResults {
    private List<SearchResult> mSearchResultList;
    private String mNextPageUrl;
    public SearchResults(List<SearchResult> searchResultList) {
        mSearchResultList = searchResultList;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.mNextPageUrl = nextPageUrl;
    }

    public String getNextPageUrl() {
        return mNextPageUrl;
    }

    public List<SearchResult> getSearchResultList() {
        return mSearchResultList;
    }
}
