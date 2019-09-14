package com.ruslanlyalko.wl.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.ruslanlyalko.wl.R;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Ruslan Lyalko
 * on 20.09.2018.
 */

public class ChooserDialog extends BottomSheetDialogFragment implements DialogInterface.OnShowListener {

    private static final String KEY_RESOURCE = "res";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    @BindView(R.id.navigationView) protected NavigationView mNavigationView;
    @BindView(R.id.title) protected TextView mTextTitle;
    @BindView(R.id.subtitle) protected TextView mTextSubTitle;
    @BindView(R.id.textLetters) protected TextView mTextLetters;
    private Unbinder unbinder;
    private OnItemSelectedListener listener;
    @MenuRes
    private int menuRes;
    private String title;
    private String subtitle;

    public static ChooserDialog newInstance(String title, String subtitle, @MenuRes int resId, OnItemSelectedListener listener) {
        ChooserDialog dialog = new ChooserDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RESOURCE, resId);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_SUBTITLE, subtitle);
        dialog.setArguments(bundle);
        dialog.setListener(listener);
        return dialog;
    }

    @Override
    public int getTheme() {
        return R.style.ChooserTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) parseArguments(getArguments());
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroyView();
    }

    protected void parseArguments(Bundle args) {
        title = args.getString(KEY_TITLE);
        subtitle = args.getString(KEY_SUBTITLE);
        menuRes = args.getInt(KEY_RESOURCE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chooser, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavigationView.inflateMenu(menuRes);
        mNavigationView.setNavigationItemSelectedListener(this::onItemSelected);
        if (TextUtils.isEmpty(title)) {
            mTextTitle.setVisibility(View.GONE);
        } else {
            mTextTitle.setText(title);
            mTextLetters.setText(getAbbreviation(title));
        }
        if (TextUtils.isEmpty(subtitle)) {
            mTextSubTitle.setVisibility(View.GONE);
        } else {
            mTextSubTitle.setText(subtitle);
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(this);
        return dialog;
    }

    private String getAbbreviation(final String name) {
        if (TextUtils.isEmpty(name)) return "";
        String[] list = name.split(" ");
        String result = list[0].substring(0, 1);
        if (list.length > 1)
            result = result + list[1].substring(0, 1);
        return result.toUpperCase();
    }

    protected boolean onItemSelected(@NonNull MenuItem item) {
        listener.onNavigationItemSelected(item);
        dismiss();
        return true;
    }

    public void setListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onShow(final DialogInterface dialog) {
        try {
            getDialog()
                    .getWindow()
                    .findViewById(R.id.design_bottom_sheet)
                    .setBackgroundResource(android.R.color.transparent);
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public interface OnItemSelectedListener {

        void onNavigationItemSelected(@NonNull MenuItem item);
    }
}
