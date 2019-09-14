package com.ruslanlyalko.wl.presentation.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ruslanlyalko.wl.R;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView<P> {

    private Unbinder mUnbinder;
    private P mPresenter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        if (alwaysNight() || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppTheme_Dark);
        }
        super.onCreate(savedInstanceState);
        int viewId = getContentView();
        if (viewId != -1) {
            setContentView(getContentView());
            mUnbinder = ButterKnife.bind(this);
        }
        initPresenter(getIntent());
        if (mPresenter == null) {
            throw new RuntimeException("Please init presenter!");
        }
        getPresenter().attachView(this);
        setSupportActionBar(getToolbar());
        if (hasHomeButton() && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (getHomeIndicator() > 0)
                getSupportActionBar().setHomeAsUpIndicator(getHomeIndicator());
        }
        onViewReady(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        getPresenter().detachView();
        if (mUnbinder != null)
            mUnbinder.unbind();
        super.onDestroy();
    }

    ;

    protected boolean alwaysNight() {return false;}

    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onHomeClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected int getHomeIndicator() {
        return R.drawable.ic_arrow_back;
    }

    protected Toolbar getToolbar() {return null;}

    protected boolean hasHomeButton() {return true;}

    protected void setToolbarTitle(@StringRes int titleRes) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleRes);
        }
    }

    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideKeyboard();
    }

    protected int getContentView() {return -1;}

    protected void onHomeClicked() {
        onBackPressed();
    }

    protected void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(200);
        }
    }

    protected void onFabClickedFragment() {
        FragmentManager fm = getSupportFragmentManager();
        BaseFragment frag = (BaseFragment) fm.findFragmentById(R.id.container);
        if (frag != null) {
            frag.onFabClicked();
        }
    }

    protected void onDeleteClickedFragment() {
        FragmentManager fm = getSupportFragmentManager();
        BaseFragment frag = (BaseFragment) fm.findFragmentById(R.id.container);
        if (frag != null) {
            frag.onDeleteClicked();
        }
    }

    public Context getContext() {
        return this;
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (imm != null && view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void forceRippleAnimation(View view) {
        Drawable background = view.getBackground();
        final RippleDrawable rippleDrawable = (RippleDrawable) background;
        rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
        Handler handler = new Handler();
        handler.postDelayed(() -> rippleDrawable.setState(new int[]{}), 300);
    }

    protected abstract void initPresenter(final Intent intent);

    protected abstract void onViewReady(final Bundle savedInstanceState);

    public P getPresenter() {
        return mPresenter;
    }

    public void setPresenter(final P presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String text) {
        vibrate();
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void showFab() {}

    public void hideFab() {}
}
