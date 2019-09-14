package com.ruslanlyalko.wl.presentation.ui.main;

import com.ruslanlyalko.wl.data.models.User;
import com.ruslanlyalko.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MainPresenter extends BasePresenter<MainView> {

    private User mUser = new User();
    private boolean mStartWithSettings;

    MainPresenter(final boolean startWithSettings) {
        mStartWithSettings = startWithSettings;
    }

    public boolean isStartWithSettings() {
        return mStartWithSettings;
    }

    public void setStartWithSettings(final boolean startWithSettings) {
        mStartWithSettings = startWithSettings;
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
        getDataManager().updateVersion();
        getDataManager().updateToken();
        getDataManager().updateNightMode(getView().isNightMode());
    }

    public void onFabClicked() {
        getView().fabClickedFragment();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
        if(mUser.getIsAdmin()) {
            getDataManager().getAllUsers();
        }
        if(mUser.getIsBlocked()) {
            getDataManager().isBlocked().addOnSuccessListener(checkBlocked -> {
                if(checkBlocked.getIsBlocked()) {
                    getAuth().signOut();
                    getView().showErrorAndStartLoginScreen();
                }
            });
        }
    }

    public void onLogout() {
        getDataManager().logout();
        getView().showLoginScreen();
    }
}
