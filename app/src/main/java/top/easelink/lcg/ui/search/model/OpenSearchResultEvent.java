package top.easelink.lcg.ui.search.model;

public class OpenSearchResultEvent {

    private SearchResult mSearchResult;

    public OpenSearchResultEvent(SearchResult searchResult) {
        mSearchResult = searchResult;
    }

    public SearchResult getSearchResult() {
        return mSearchResult;
    }
}