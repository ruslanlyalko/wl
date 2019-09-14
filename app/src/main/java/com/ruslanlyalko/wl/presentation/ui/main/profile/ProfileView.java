package com.ruslanlyalko.wl.presentation.ui.main.profile;

import com.ruslanlyalko.wl.data.models.User;
import com.ruslanlyalko.wl.presentation.base.BaseView;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProfileView extends BaseView<ProfilePresenter> {

    void startProfileEditScreen();

    void showUser(MutableLiveData<User> myUserData);

    void populateUser(User user);

    void showLoginScreen();
}
