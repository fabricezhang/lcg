package top.easelink.lcg.ui.search.viewmodel;

import androidx.databinding.ObservableField;
import org.greenrobot.eventbus.EventBus;
import top.easelink.lcg.ui.search.model.OpenSearchResultEvent;
import top.easelink.lcg.ui.search.model.SearchResult;

public class SearchResultItemViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> content;

    private final SearchResult searchResult;

    SearchResultItemViewModel(SearchResult searchResult) {
        this.searchResult = searchResult;
        title = new ObservableField<>(searchResult.getTitle());
        content = new ObservableField<>(searchResult.getContentAbstractt());
    }

    public void onItemClick() {
        OpenSearchResultEvent event = new OpenSearchResultEvent(searchResult);
        EventBus.getDefault().post(event);
    }
}
