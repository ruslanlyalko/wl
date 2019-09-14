package com.ruslanlyalko.wl.presentation.ui.main.profile.edit;

import com.ruslanlyalko.wl.data.models.User;
import com.ruslanlyalko.wl.presentation.base.BaseView;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProfileEditView extends BaseView<ProfileEditPresenter> {

    void showUser(MutableLiveData<User> user);

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving();
}
