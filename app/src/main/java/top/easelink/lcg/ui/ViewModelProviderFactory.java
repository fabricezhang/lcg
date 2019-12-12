package top.easelink.lcg.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.search.viewmodel.SearchViewModel;

@Singleton
public class ViewModelProviderFactory extends ViewModelProvider.NewInstanceFactory {

    private SchedulerProvider schedulerProvider;

    @Inject
    public ViewModelProviderFactory(SchedulerProvider schedulerProvider) {
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(schedulerProvider);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}