package top.easelink.lcg.ui.main.article.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.AndroidSupportInjection;
import top.easelink.framework.base.BaseDialog;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.DialogDownloadLinkBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.article.viewmodel.DownloadLinkViewModel;

import javax.inject.Inject;
import java.util.ArrayList;

import static top.easelink.lcg.utils.ActivityUtils.TAG_PREFIX;

public class DownloadLinkDialog extends BaseDialog implements DownloadLinkCallBack {

    private static final String TAG = TAG_PREFIX + DownloadLinkDialog.class.getSimpleName();

    private static final String KEY_LINK_LIST = "KEY_LINK_LIST";
    @Inject
    ViewModelProviderFactory factory;
    private DialogDownloadLinkBinding mViewBinding;

    static DownloadLinkDialog newInstance(ArrayList<String> downloadLinkList) {
        DownloadLinkDialog fragment = new DownloadLinkDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_LINK_LIST, downloadLinkList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void dismissDialog() {
        super.dismissDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_download_link, container, false);
        View view = mViewBinding.getRoot();
        AndroidSupportInjection.inject(this);
        DownloadLinkViewModel mDownloadLinkViewModel = ViewModelProviders.of(this,factory).get(DownloadLinkViewModel.class);
        mViewBinding.setViewModel(mDownloadLinkViewModel);
        mDownloadLinkViewModel.setNavigator(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<String> linkList = bundle.getStringArrayList(KEY_LINK_LIST);
            if (linkList != null && !linkList.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseActivity(),
                        R.layout.download_link_item_view, linkList);
                mViewBinding.downloadLinkList.setAdapter(adapter);
                mViewBinding.downloadLinkList.setOnItemClickListener( (parent, itemView, position, id) -> {
                    copy(linkList.get(position));
                    itemView.setBackgroundColor(getBaseActivity().getResources().getColor(R.color.colorAccent));
                });
            }
        }
    }

    void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    private void copy(String clipString) {
        ClipboardManager cm = (ClipboardManager) getBaseActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("DownloadLink", clipString);
        if (cm != null) {
            cm.setPrimaryClip(mClipData);
            showMessage(R.string.download_link_copy_succeed);
        } else {
            showMessage(R.string.download_link_copy_failed);
        }
    }

    private void showMessage(@StringRes int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }
}
