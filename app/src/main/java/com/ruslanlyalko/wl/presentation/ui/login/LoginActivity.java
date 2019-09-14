package com.ruslanlyalko.wl.presentation.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ruslanlyalko.wl.R;
import com.ruslanlyalko.wl.presentation.base.BaseActivity;
import com.ruslanlyalko.wl.presentation.ui.main.MainActivity;
import com.ruslanlyalko.wl.presentation.view.ProgressButton;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView {

    @BindView(R.id.input_email) TextInputEditText mInputEmail;
    @BindView(R.id.input_password) TextInputEditText mInputPassword;
    @BindView(R.id.button_login) ProgressButton mButtonLogin;
    @BindView(R.id.text_forgot) TextView mTextForgot;
    @BindView(R.id.layout_root) LinearLayout mLayoutRoot;
    @BindView(R.id.image_logo) ImageView mImageLogo;
    boolean isShowedAnimation;
    @BindView(R.id.input_layout_email) TextInputLayout mInputLayoutEmail;
    @BindView(R.id.input_layout_password) TextInputLayout mInputLayoutPassword;
    private String mEmailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected boolean alwaysNight() {
        return true;
    }

    @Override
    protected boolean hasHomeButton() {
        return false;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new LoginPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        mInputPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onLoginClick();
                return true;
            }
            return false;
        });
        getPresenter().onViewReady();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isShowedAnimation) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_login);
            mLayoutRoot.startAnimation(animation);
            Animation animationLogo = AnimationUtils.loadAnimation(this, R.anim.anim_login_logo);
            mImageLogo.startAnimation(animationLogo);
            isShowedAnimation = true;
        }
    }

    @Override
    public void showForgotPasswordButton() {
        mTextForgot.setVisibility(View.VISIBLE);
    }

    @Override
    public void startMainScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager afm = getSystemService(AutofillManager.class);
            if (afm != null) {
                afm.commit();
            }
        }
        startActivity(MainActivity.getLaunchIntent(this));
        finish();
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mButtonLogin.showProgress(true);
    }

    @Override
    public void hideProgress() {
        mButtonLogin.showProgress(false);
    }

    @Override
    public void showBlockedError() {
        Toast.makeText(getContext(), R.string.error_blocked, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showInternetError() {
        showError(getString(R.string.error_no_internet));
    }

    @Override
    public void errorEmptyEmail() {
        mInputLayoutEmail.setError(getString(R.string.error_cant_be_empty));
    }

    @Override
    public void errorEmptyPassword() {
        mInputLayoutPassword.setError(getString(R.string.error_cant_be_empty));
    }

    @Override
    public void errorWrongEmail() {
        mInputLayoutEmail.setError(getString(R.string.error_email_badly_formatted));
    }

    @OnClick(R.id.button_login)
    public void onLoginClick() {
        getPresenter().onLogin(String.valueOf(mInputEmail.getText().toString().trim()), String.valueOf(mInputPassword.getText().toString().trim()));
    }

    @OnTextChanged(R.id.input_email)
    void onEmailChanged(CharSequence text) {
        if (!TextUtils.isEmpty(text) && text.toString().trim().matches(mEmailPattern))
            mInputLayoutEmail.setError(null);
    }

    @OnTextChanged(R.id.input_password)
    void onPasswordChanged(CharSequence text) {
        if (!TextUtils.isEmpty(text))
            mInputLayoutPassword.setError(null);
    }

    @OnClick(R.id.text_forgot)
    public void onClick() {
        getPresenter().onForgot(mInputEmail.getText().toString().trim());
    }
}
