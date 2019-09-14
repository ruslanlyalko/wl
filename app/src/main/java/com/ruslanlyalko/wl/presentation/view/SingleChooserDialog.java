package com.ruslanlyalko.wl.presentation.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Ruslan Lyalko
 * on 20.09.2018.
 */

public class SingleChooserDialog extends ChooserDialog {

    private static final String KEY_RESOURCE = "res";
    private static final String KEY_RESOURCE_ID = "resId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    @IdRes
    private int itemId;

    public static SingleChooserDialog newInstance(String title, String subtitle, @MenuRes int resId, @IdRes int itemId, OnItemSelectedListener listener) {
        SingleChooserDialog dialog = new SingleChooserDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RESOURCE, resId);
        bundle.putInt(KEY_RESOURCE_ID, itemId);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_SUBTITLE, subtitle);
        dialog.setArguments(bundle);
        dialog.setListener(listener);
        return dialog;
    }

    @Override
    protected void parseArguments(Bundle args) {
        super.parseArguments(args);
        itemId = args.getInt(KEY_RESOURCE_ID);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavigationView.setCheckedItem(itemId);
    }

    @Override
    protected boolean onItemSelected(@NonNull MenuItem item) {
        return !item.isChecked() && super.onItemSelected(item);
    }
}
