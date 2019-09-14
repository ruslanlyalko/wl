package com.ruslanlyalko.wl.presentation.ui.login;

import android.text.TextUtils;

import com.ruslanlyalko.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private String mEmailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    LoginPresenter() {
    }

    public void onViewReady() {
    }

    public void onLogin(final String email, final String password) {
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            getView().errorEmptyEmail();
            getView().errorEmptyPassword();
            return;
        }
        if(TextUtils.isEmpty(email)) {
            getView().errorEmptyEmail();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            getView().errorEmptyPassword();
            return;
        }
        if(!email.matches(mEmailPattern)) {
            getView().errorWrongEmail();
            return;
        }
        getView().showProgress();
        getAuth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    getDataManager().isBlocked().addOnSuccessListener(checkBlocked -> {
                        if(checkBlocked.getIsBlocked()) {
                            getView().hideProgress();
                            getAuth().signOut();
                            getView().showBlockedError();
                        } else {
                            getDataManager().updateToken();
                            getDataManager().getMyUser();
                            getView().startMainScreen();
                        }
                    }).addOnFailureListener(e -> {
                        getView().hideProgress();
                        getView().showInternetError();
                    });
                })
                .addOnFailureListener(e -> {
                    getView().hideProgress();
                    getView().showForgotPasswordButton();
                    getView().showError(e.getLocalizedMessage());
                });
    }

    public void onForgot(final String email) {
        if(TextUtils.isEmpty(email)) {
            getView().errorEmptyEmail();
            return;
        }
        if(!email.matches(mEmailPattern)) {
            getView().errorWrongEmail();
            return;
        }
        getAuth().sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> getView().showMessage("Email sent!"))
                .addOnFailureListener(e -> getView().showError(e.getLocalizedMessage()));
    }
}
