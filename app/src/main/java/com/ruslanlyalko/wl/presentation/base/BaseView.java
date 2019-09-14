package com.ruslanlyalko.wl.presentation.base;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface BaseView<P extends BasePresenter> {

    void showMessage(String text);

    void showError(String text);
}
