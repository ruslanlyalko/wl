package com.ruslanlyalko.wl.presentation.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ruslanlyalko.wl.presentation.base.BaseActivity;
import com.ruslanlyalko.wl.presentation.ui.login.LoginActivity;
import com.ruslanlyalko.wl.presentation.ui.main.MainActivity;

public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashView {

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, SplashActivity.class);
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new SplashPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        getPresenter().onViewReady();
    }

    @Override
    public void startDashboardScreen() {
        startActivity(MainActivity.getLaunchIntent(this));
        finish();
    }

    @Override
    public void startLoginScreen() {
        startActivity(LoginActivity.getLaunchIntent(this));
        finish();
    }
}
