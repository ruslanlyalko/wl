package com.ruslanlyalko.wl.presentation.ui.main.profile.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ruslanlyalko.wl.R;
import com.ruslanlyalko.wl.data.models.User;
import com.ruslanlyalko.wl.presentation.base.BaseActivity;
import com.ruslanlyalko.wl.presentation.utils.DateUtils;
import com.ruslanlyalko.wl.presentation.view.SquareButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ProfileEditActivity extends BaseActivity<ProfileEditPresenter> implements ProfileEditView {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.scroll_view) ScrollView mScrollView;
    @BindView(R.id.input_layout_phone) TextInputLayout mInputLayoutPhone;
    @BindView(R.id.input_layout_skype) TextInputLayout mInputLayoutSkype;
    @BindView(R.id.input_phone) TextInputEditText mInputPhone;
    @BindView(R.id.input_skype) TextInputEditText mInputSkype;

    @BindView(R.id.input_birthday) TextInputEditText mInputBirthday;
    @BindView(R.id.input_password) TextInputEditText mInputPassword;
    @BindView(R.id.input_layout_password) TextInputLayout mInputLayoutPassword;
    @BindView(R.id.input_password_confirm) TextInputEditText mInputPasswordConfirm;
    @BindView(R.id.input_layout_password_confirm) TextInputLayout mInputLayoutPasswordConfirm;

    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;
    private boolean mIsChanged;
    private Date mBirthDay = new Date();

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, ProfileEditActivity.class);
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void onBackPressed() {
        if (mIsChanged) {
            AlertDialog.Builder build = new AlertDialog.Builder(getContext());
            build.setMessage(R.string.text_save_changes);
            build.setPositiveButton(R.string.action_save, (dialog, which) -> {
                dialog.dismiss();
                onSaveClick();
            });
            build.setNegativeButton(R.string.action_discard, (dialog, which) -> {
                dialog.dismiss();
                super.onBackPressed();
            });
            build.show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_profile_edit;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ProfileEditPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_profile);
        getPresenter().onViewReady();
    }

    @Override
    public void showUser(final MutableLiveData<User> userData) {
        userData.observe(this, user -> {
            if (user != null) {
                getPresenter().setUser(user);
                setToolbarTitle(user.getName());
                mInputPhone.setText(user.getPhone());
                mInputSkype.setText(user.getSkype());
                mBirthDay = user.getBirthday();
                mInputBirthday.setText(DateUtils.toStringStandardDate(mBirthDay));
                mIsChanged = false;
            }
        });
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
        mButtonSave.showProgress(true);
    }

    @Override
    public void hideProgress() {
        mButtonSave.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void afterSuccessfullySaving() {
        showMessage(getString(R.string.text_report_saved));
        onBackPressed();
    }

    @OnTextChanged(R.id.input_phone)
    public void onPhoneTextChanged(CharSequence text) {
        if (!text.toString().isEmpty()) {
            mInputLayoutPhone.setError(null);
        }
        mIsChanged = true;
    }

    @OnTextChanged(R.id.input_skype)
    public void onSkypeTextChanged(CharSequence text) {
        if (!text.toString().isEmpty()) {
            mInputLayoutSkype.setError(null);
        }
        mIsChanged = true;
    }

    @OnTextChanged(R.id.input_password)
    public void onPasswordTextChanged(CharSequence text) {
        if (text.toString().isEmpty() || text.toString().length() >= 6) {
            mInputLayoutPassword.setError(null);
        }
    }

    @OnTextChanged(R.id.input_password_confirm)
    public void onPasswordCTextChanged(CharSequence text) {
        if (text.toString().isEmpty() || text.toString().equals(mInputPassword.getText().toString())) {
            mInputLayoutPasswordConfirm.setError(null);
        }
    }

    @OnClick(R.id.button_save)
    public void onSaveClick() {
        boolean isEmpty = false;
        if (TextUtils.isEmpty(mInputSkype.getText())) {
            mInputLayoutSkype.setError(getString(R.string.error_cant_be_empty));
            mInputSkype.requestFocus();
            isEmpty = true;
        }
        if (TextUtils.isEmpty(mInputPhone.getText())) {
            mInputLayoutPhone.setError(getString(R.string.error_cant_be_empty));
            mInputPhone.requestFocus();
            isEmpty = true;
        }
        if (isEmpty) return;
        // Password
        if (!TextUtils.isEmpty(mInputPassword.getText()) && mInputPassword.getText().toString().length() < 6) {
            mInputLayoutPassword.setError(getString(R.string.error_passwords_should_be_at_least_6_symbols));
            mInputPassword.requestFocus();
        } else if (!TextUtils.isEmpty(mInputPassword.getText()) && !mInputPassword.getText().toString().equals(mInputPasswordConfirm.getText().toString())) {
            mInputLayoutPasswordConfirm.setError(getString(R.string.error_passwords_should_equals));
            mScrollView.postDelayed(() -> {
                mScrollView.fullScroll(View.FOCUS_DOWN);
                mInputPasswordConfirm.requestFocus();
            }, 50);
            mInputPasswordConfirm.requestFocus();
        } else
            getPresenter().onSave(mBirthDay, mInputSkype.getText().toString(), mInputPhone.getText().toString(), mInputPassword.getText().toString());
    }

    @OnClick(R.id.input_birthday)
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mBirthDay);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
            Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
            mBirthDay = newDate;
            mInputBirthday.setText(DateUtils.toStringStandardDate(newDate));
            mIsChanged = true;
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(DateUtils.getYesterday());
        datePickerDialog.showYearPickerFirst(true);
        datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
        datePickerDialog.show(getFragmentManager(), "birthday");
    }
}
