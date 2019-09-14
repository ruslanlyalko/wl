package com.ruslanlyalko.wl.presentation.ui.splash;

import com.ruslanlyalko.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class SplashPresenter extends BasePresenter<SplashView> {

    SplashPresenter() {
    }

    public void onViewReady() {
        if(getCurrentUser() != null) {
            getView().startDashboardScreen();
        } else {
            getView().startLoginScreen();
        }
    }
}
