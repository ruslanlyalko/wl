package com.ruslanlyalko.wl.presentation.ui.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruslanlyalko.wl.R;
import com.ruslanlyalko.wl.data.models.User;
import com.ruslanlyalko.wl.presentation.base.BaseFragment;
import com.ruslanlyalko.wl.presentation.ui.login.LoginActivity;
import com.ruslanlyalko.wl.presentation.ui.main.profile.edit.ProfileEditActivity;
import com.ruslanlyalko.wl.presentation.utils.DateUtils;
import com.ruslanlyalko.wl.presentation.utils.ImageLoader;

import androidx.lifecycle.MutableLiveData;
import butterknife.BindView;

public class ProfileFragment extends BaseFragment<ProfilePresenter> implements ProfileView {

    @BindView(R.id.imageUserProfile) ImageView mImageUserProfile;
    @BindView(R.id.text_name) TextView mTextName;
    @BindView(R.id.text_email) TextView mTextEmail;
    @BindView(R.id.text_phone) TextView mTextPhone;
    @BindView(R.id.text_skype) TextView mTextSkype;
    @BindView(R.id.text_department) TextView mTextDepartment;
    @BindView(R.id.text_birthday) TextView mTextBirthday;
    @BindView(R.id.text_first) TextView mTextFirst;
    private boolean isShowedAnimation;

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if(!isShowedAnimation) {
//            Animation animation1 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_profile);
//            mTextName.startAnimation(animation1);
//            Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_profile);
//            animation2.setStartOffset(50);
//            mTextDepartment.startAnimation(animation2);
//            Animation animation3 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_profile);
//            animation3.setStartOffset(100);
//            mTextEmail.startAnimation(animation3);
//            Animation animation4 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_profile);
//            animation4.setStartOffset(150);
//            mTextFirst.startAnimation(animation4);
//            Animation animation5 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_profile);
//            animation5.setStartOffset(200);
//            mTextPhone.startAnimation(animation5);
//            Animation animation6 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_profile);
//            animation6.setStartOffset(250);
//            mTextSkype.startAnimation(animation6);
//            Animation animation7 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_profile);
//            animation7.setStartOffset(300);
//            mTextBirthday.startAnimation(animation7);
//            isShowedAnimation = true;
//        }
//    }

    @Override
    public void startProfileEditScreen() {
        startActivity(ProfileEditActivity.getLaunchIntent(getContext()));
    }

    @Override
    public void showUser(final MutableLiveData<User> myUserData) {
        myUserData.observe(this, user -> getPresenter().setUser(user));
    }

    @Override
    public void populateUser(final User user) {
        ImageLoader.loadCirclePhoto(user.getAvatar(), mImageUserProfile);
        mTextName.setText(user.getName());
        mTextEmail.setText(user.getEmail());
        if (TextUtils.isEmpty(user.getPhone())) {
            mTextPhone.setText(R.string.text_not_specified);
        } else {
            mTextPhone.setText(user.getPhone());
        }
        if (TextUtils.isEmpty(user.getSkype())) {
            mTextSkype.setText(R.string.text_not_specified);
        } else {
            mTextSkype.setText(user.getSkype());
        }
        mTextDepartment.setText(user.getDepartment());
        mTextBirthday.setText(DateUtils.toStringStandardDate(user.getBirthday()));
        mTextFirst.setText(DateUtils.toStringStandardDate(user.getFirstWorkingDate()));
    }

    @Override
    public void showLoginScreen() {
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.action_edit:
                getPresenter().onEditClicked();
                return true;
            case R.id.action_logout:
//                ((MainActivity) getBaseActivity()).onShowLogoutMenu();
                return true;
            case R.id.action_menu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new ProfilePresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.app_name);
        showFab();
        getPresenter().onViewReady();
    }
}
