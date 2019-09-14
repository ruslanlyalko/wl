package com.ruslanlyalko.wl.presentation.ui.login;

import com.ruslanlyalko.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface LoginView extends BaseView<LoginPresenter> {

    void showForgotPasswordButton();

    void startMainScreen();

    void showProgress();

    void hideProgress();

    void showBlockedError();

    void showInternetError();

    void errorEmptyEmail();

    void errorEmptyPassword();

    void errorWrongEmail();
}
