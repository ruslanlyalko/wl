package com.ruslanlyalko.wl.data;

import com.google.android.gms.tasks.Task;
import com.ruslanlyalko.wl.data.models.AppSettings;
import com.ruslanlyalko.wl.data.models.CheckBlocked;
import com.ruslanlyalko.wl.data.models.CheckDate;
import com.ruslanlyalko.wl.data.models.User;

import java.util.List;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface DataManager {

    //Users
    Task<Void> saveUser(User user);

    MutableLiveData<User> getMyUser();

    MutableLiveData<User> getUser(String key);

    MutableLiveData<List<User>> getAllUsers();

    Task<Void> changePassword(String newPassword);

    void updateRemindMeAt(final String remindMeAt);

    void updateDefaultWorkingTime(final int defaultWorkingTime);

    void updateOldStyleCalendar(final boolean isEnabled);

    void updateVersion();

    void updateNightMode(final boolean isNightMode);

    void updateToken();

    void logout();

    void clearCache();

    Task<CheckBlocked> isBlocked();

    Task<CheckDate> isRightDate();

    MutableLiveData<AppSettings> getSettings();

    Task<Void> setSettings(AppSettings settings);
}
