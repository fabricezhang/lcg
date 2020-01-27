package top.easelink.framework.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import top.easelink.framework.topbase.ControllableFragment;
import top.easelink.framework.topbase.TopActivity;

public abstract class BaseFragment<T extends ViewDataBinding, V extends ViewModel> extends Fragment implements ControllableFragment {

    private AppCompatActivity mActivity;
    private T mViewDataBinding;
    private V mViewModel;


    @Override
    public boolean isControllable() {
        return true;
    }

    @NotNull
    @Override
    public String getBackStackTag() {
        return this.getClass().getSimpleName();
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            if (this.isControllable()) {
                activity.onFragmentAttached(getBackStackTag());
            }
        } else if (context instanceof TopActivity) {
            TopActivity topActivity = (TopActivity) context;
            this.mActivity = topActivity;
            if (isControllable()) {
                topActivity.onFragmentAttached(getBackStackTag());
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = getViewModel();
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        View mRootView = mViewDataBinding.getRoot();
        mViewDataBinding.setLifecycleOwner(getActivity());
        return mRootView;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.setLifecycleOwner(this);
        mViewDataBinding.executePendingBindings();
    }

    protected AppCompatActivity getBaseActivity() {
        return mActivity;
    }

    protected T getViewDataBinding() {
        return mViewDataBinding;
    }
}
