package com.ruslanlyalko.wl.presentation.ui.main;

import com.ruslanlyalko.wl.data.models.User;
import com.ruslanlyalko.wl.presentation.base.BaseView;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MainView extends BaseView<MainPresenter> {

    void showUser(MutableLiveData<User> myUserData);

    void showMenu(User user);

    void fabClickedFragment();

    void showErrorAndStartLoginScreen();

    void showLoginScreen();

    boolean isNightMode();
}
