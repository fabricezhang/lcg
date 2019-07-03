package top.easelink.lcg.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.viewmodel.MainViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ViewModelProviderFactory extends ViewModelProvider.NewInstanceFactory {

  private final SchedulerProvider schedulerProvider;

  @Inject
  public ViewModelProviderFactory(SchedulerProvider schedulerProvider) {
    this.schedulerProvider = schedulerProvider;
  }


  @Override
  @NonNull
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(MainViewModel.class)) {
      //noinspection unchecked
      return (T) new MainViewModel(schedulerProvider);
    }
    throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
  }
}