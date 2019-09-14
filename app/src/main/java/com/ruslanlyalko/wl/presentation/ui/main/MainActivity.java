package com.ruslanlyalko.wl.presentation.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ruslanlyalko.wl.R;
import com.ruslanlyalko.wl.data.models.User;
import com.ruslanlyalko.wl.presentation.base.multibackstack.BackStackActivity;
import com.ruslanlyalko.wl.presentation.ui.login.LoginActivity;
import com.ruslanlyalko.wl.presentation.ui.main.profile.ProfileFragment;
import com.ruslanlyalko.wl.presentation.view.ChooserDialog;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BackStackActivity<MainPresenter> implements MainView {

    private static final int TAB_PROFILE = 0;
    private static final int TAB_CALENDAR = 1;
    private static final int TAB_MEMBERS = 2;
    private static final int TAB_EXPENSES = 3;
    private static final int TAB_SALARY = 4;
    private static final int TAB_SETTINGS = 5;
    private static final int TAB_CHAT = 6;
    private static final String STATE_CURRENT_TAB_ID = "current_tab_id";
    private static final String KEY_SETTINGS = "settings";
    private static final String TAG_FRAGMENT_MENU = "menu";

    @BindView(R.id.bottom_app_bar) BottomAppBar mBottomAppBar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.image_menu) AppCompatImageView mImageMenu;
    private float mOldY;
    private Fragment mCurFragment;
    private int mCurTabId = TAB_CALENDAR;
    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        private static final int MIN_DISTANCE = 100;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mOldY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float y2 = event.getY();
                    float deltaY = y2 - mOldY;
//                    if (deltaY > MIN_DISTANCE) {
//                        // top2bottom
//                    } else
                    if (deltaY < (0 - MIN_DISTANCE)) {
                        onHomeClicked();
                    }
//                    else {
//                        v.performClick();
//                    }
                    break;
            }
            return false;
        }
    };

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static Intent getLaunchIntent(Context context, boolean startWithSettings) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_SETTINGS, startWithSettings);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        getPresenter().onFabClicked();
    }

    @Override
    public void showUser(final MutableLiveData<User> myUserData) {
        myUserData.observe(this, user -> {
            getPresenter().setUser(user);
        });
    }

    @Override
    public void showMenu(final User user) {
        if (getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_MENU) != null) {
            return;
        }
        ChooserDialog.newInstance(getPresenter().getUser().getName(), getPresenter().getUser().getEmail(), R.menu.menu_nav, item1 -> {
            switch (item1.getItemId()) {
                case R.id.action_profile:
                    onTabSelected(TAB_PROFILE);
                    break;
                case R.id.action_calendar:
                    onTabSelected(TAB_CALENDAR);
                    break;
                case R.id.action_members:
                    onTabSelected(TAB_MEMBERS);
                    break;
                case R.id.action_expenses:
                    onTabSelected(TAB_EXPENSES);
                    break;
                case R.id.action_salary:
                    onTabSelected(TAB_SALARY);
                    break;
                case R.id.action_settings:
                    onTabSelected(TAB_SETTINGS);
                    break;
            }
        }).show(getSupportFragmentManager(), TAG_FRAGMENT_MENU);
    }

    @Override
    public void fabClickedFragment() {
        onFabClickedFragment();
    }

    @Override
    public void showErrorAndStartLoginScreen() {
        Toast.makeText(getContext(), R.string.error_blocked, Toast.LENGTH_LONG).show();
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void showLoginScreen() {
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public boolean isNightMode() {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    }

    private int getMenuIdByTab(final int tabId) {
        switch (tabId) {
            case TAB_PROFILE:
                return TAB_PROFILE;
            case TAB_CALENDAR:
                return R.id.action_workload;
            case TAB_MEMBERS:
                return R.id.action_vacations;
            case TAB_EXPENSES:
                return R.id.action_alerts;
            case TAB_SALARY:
                return R.id.action_dashboard;
            case TAB_CHAT:
                return R.id.action_projects;
            case TAB_SETTINGS:
                return R.id.action_settings;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void onTabSelected(int tabId) {
        if (mCurFragment != null) {
            pushFragmentToBackStack(mCurTabId, mCurFragment);
        }
        mCurTabId = tabId;
        Fragment fragment = popFragmentFromBackStack(mCurTabId);
        if (fragment == null) {
            fragment = rootTabFragment(mCurTabId);
        }
        replaceFragment(fragment);
    }

    @NonNull
    private Fragment rootTabFragment(int tabId) {
        switch (tabId) {
            case TAB_PROFILE:
                return ProfileFragment.newInstance();
            case TAB_CALENDAR:
                //todo
                return ProfileFragment.newInstance();
            case TAB_MEMBERS:
                return ProfileFragment.newInstance();
            case TAB_EXPENSES:
                return ProfileFragment.newInstance();
            case TAB_SALARY:
                return ProfileFragment.newInstance();
            case TAB_CHAT:
                return ProfileFragment.newInstance();
            case TAB_SETTINGS:
                return ProfileFragment.newInstance();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_my_notes:
                onTabSelected(TAB_CHAT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return mBottomAppBar;
    }

    @Override
    public void onBackPressed() {
        if (mCurTabId != TAB_PROFILE) {
            onTabSelected(TAB_PROFILE);
        } else
            super.onBackPressed();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onHomeClicked() {
        showMenu(getPresenter().getUser());
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new MainPresenter(intent.getBooleanExtra(KEY_SETTINGS, false)));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onViewReady(final Bundle state) {
        mImageMenu.setOnTouchListener(mOnTouchListener);
        mBottomAppBar.setOnTouchListener(mOnTouchListener);
        mBottomAppBar.setNavigationIcon(null);
        getPresenter().onViewReady();
        if (state == null) {
            mCurTabId = getPresenter().isStartWithSettings() ? TAB_SETTINGS : TAB_PROFILE;
            showFragment(rootTabFragment(mCurTabId));
        }
    }

    @Override
    public void showFab() {
        mFab.show();
        mFab.setEnabled(true);
    }

    @Override
    public void hideFab() {
        mFab.hide();
        mFab.setEnabled(false);
    }

    public void showFragment(@NonNull Fragment fragment) {
        showFragment(fragment, true);
    }

    public void showFragment(@NonNull Fragment fragment, boolean addToBackStack) {
        if (mCurFragment != null && addToBackStack) {
            pushFragmentToBackStack(mCurTabId, mCurFragment);
        }
        replaceFragment(fragment);
    }

    private void backTo(int tabId, @NonNull Fragment fragment) {
        if (tabId != mCurTabId) {
            mCurTabId = tabId;
            //select tab
        }
        replaceFragment(fragment);
        getSupportFragmentManager().executePendingTransactions();
    }

    private void backToRoot() {
        if (isRootTabFragment(mCurFragment, mCurTabId)) {
            return;
        }
        resetBackStackToRoot(mCurTabId);
        Fragment rootFragment = popFragmentFromBackStack(mCurTabId);
        assert rootFragment != null;
        backTo(mCurTabId, rootFragment);
    }

    private boolean isRootTabFragment(@NonNull Fragment fragment, int tabId) {
        return fragment.getClass() == rootTabFragment(tabId).getClass();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_TAB_ID, mCurTabId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        mCurTabId = savedInstanceState.getInt(STATE_CURRENT_TAB_ID);
    }

    protected void replaceFragment(@NonNull Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        tr.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        tr.replace(R.id.container, fragment);
        tr.commitAllowingStateLoss();
        mCurFragment = fragment;
    }

    @OnClick(R.id.image_menu)
    public void onMenuClick() {
        onHomeClicked();
    }
}
