package top.easelink.lcg.ui.main.viewmodel;

import androidx.databinding.ObservableField;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.lcg.ui.main.view.MainNavigator;

public class MainViewModel extends BaseViewModel<MainNavigator> {

    private final ObservableField<String> appVersion = new ObservableField<>();

    private final ObservableField<String> userEmail = new ObservableField<>();

    private final ObservableField<String> userName = new ObservableField<>();

    private final ObservableField<String> userProfilePicUrl = new ObservableField<>();

    public MainViewModel() {

    }

    public ObservableField<String> getAppVersion() {
        return appVersion;
    }

    public ObservableField<String> getUserEmail() {
        return userEmail;
    }

    public ObservableField<String> getUserName() {
        return userName;
    }

    public ObservableField<String> getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void logout() {
        setIsLoading(true);
    }

    public void onNavMenuCreated() {

    }

    public void updateAppVersion(String version) {
        appVersion.set(version);
    }
}
