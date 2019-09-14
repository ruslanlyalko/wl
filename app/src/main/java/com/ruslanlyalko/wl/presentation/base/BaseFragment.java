package com.ruslanlyalko.wl.presentation.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruslanlyalko.wl.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView<P> {

    @Nullable
    @BindView(R.id.title)
    protected TextView mTitle;

    private Unbinder mUnbinder;
    private P mPresenter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter(getArguments());
        if(mPresenter == null) {
            throw new RuntimeException("Please init presenter!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPresenter().attachView(this);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setHasOptionsMenu(true);
        onViewReady(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().detachView();
        if(mUnbinder != null)
            mUnbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onHomeClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected int getContentView() {return -1;}

    protected void onHomeClicked() {
        getBaseActivity().onBackPressed();
    }

    protected abstract void initPresenter(final Bundle args);

    protected abstract void onViewReady(final Bundle savedInstanceState);

    public P getPresenter() {
        return mPresenter;
    }

    public void setPresenter(final P presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showMessage(String text) {
        getBaseActivity().showMessage(text);
    }

    @Override
    public void showError(String text) {
        getBaseActivity().showError(text);
    }

    protected void setToolbarTitle(String title) {
        if(mTitle != null)
            mTitle.setText(title);
        else
            getBaseActivity().setToolbarTitle(title);
    }

    protected void setToolbarTitle(@StringRes int titleRes) {
        if(mTitle != null)
            mTitle.setText(titleRes);
        else
            getBaseActivity().setToolbarTitle(titleRes);
    }

    protected void showFab() {
        getBaseActivity().showFab();
    }

    protected void hideFab() {
        getBaseActivity().hideFab();
    }

    public void onFabClicked() {}

    public void onDeleteClicked() {}

    protected void hideKeyboard() {
        getBaseActivity().hideKeyboard();
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
