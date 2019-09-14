package com.ruslanlyalko.wl.presentation.view;

import android.view.View;

/**
 * Created by Ruslan Lyalko
 * on 14.01.2018.
 */

public interface OnItemClickListener {

    void onItemClicked(View view, final int position);
    void onItemLongClicked(View view, final int position);
}
