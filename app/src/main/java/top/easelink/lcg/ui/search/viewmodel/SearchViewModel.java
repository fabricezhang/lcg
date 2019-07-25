package top.easelink.lcg.ui.search.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.search.model.SearchResult;
import top.easelink.lcg.ui.search.source.RxSearchService;
import top.easelink.lcg.ui.search.view.SearchNavigator;

import java.util.List;

import static top.easelink.lcg.utils.WebsiteConstant.BAIDU_SEARCH_BASE_URL;


public class SearchViewModel extends BaseViewModel<SearchNavigator> implements SearchResultAdapter.SearchAdapterListener {

    private final RxSearchService searchService = RxSearchService.getInstance();
    private final MutableLiveData<List<SearchResult>> searchResults = new MutableLiveData<>();
    private String mUrl;
    private String mNextPageUrl;

    public SearchViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void initUrl(String url) {
        this.mUrl = url;
        doSearchQuery(FETCH_INIT);
    }

    @Override
    public void doSearchQuery(int type) {
        setIsLoading(true);
        String requestUrl;
        if (type == FETCH_MORE) {
            if (TextUtils.isEmpty(mNextPageUrl)) {
                setIsLoading(false);
                return;
            } else {
                requestUrl = BAIDU_SEARCH_BASE_URL + mNextPageUrl;
            }
        } else {
            requestUrl = mUrl;
        }
        getCompositeDisposable().add(searchService.doSearchRequest(requestUrl)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(resultsFromService -> {
                    List<SearchResult> searchResultList = resultsFromService.getSearchResultList();
                    if (searchResultList != null && searchResultList.size()!= 0) {
                        List<SearchResult> list = searchResults.getValue();
                        mNextPageUrl = resultsFromService.getNextPageUrl();
                        if (type == FETCH_MORE && list != null && list.size() != 0) {
                            list.addAll(searchResultList);
                            searchResults.setValue(list);
                        } else {
                            searchResults.setValue(searchResultList);
                        }
                    }
                }, throwable -> {
                    setIsLoading(false);
                    getNavigator().handleError(throwable);
                }, () -> {
                    setIsLoading(false);
                }));
    }

    public LiveData<List<SearchResult>> getSearchResults() {
        return searchResults;
    }

}