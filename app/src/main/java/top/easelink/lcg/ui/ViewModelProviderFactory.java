package top.easelink.lcg.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.about.viewmodel.AboutViewModel;
import top.easelink.lcg.ui.home.viewmodel.HomeViewModel;
import top.easelink.lcg.ui.main.viewmodel.MainViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ViewModelProviderFactory extends ViewModelProvider.NewInstanceFactory {

  private final SchedulerProvider schedulerProvider;

  @Inject
  ViewModelProviderFactory(SchedulerProvider schedulerProvider) {
    this.schedulerProvider = schedulerProvider;
  }


  @Override
  @NonNull
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(MainViewModel.class)) {
      return (T) new MainViewModel(schedulerProvider);
    } else if (modelClass.isAssignableFrom(AboutViewModel.class)) {
      return (T) new AboutViewModel(schedulerProvider);
    } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
      return (T) new HomeViewModel(schedulerProvider);
    }
    throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
  }
}