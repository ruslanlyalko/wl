package com.ruslanlyalko.wl.data;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ruslanlyalko.wl.BuildConfig;
import com.ruslanlyalko.wl.data.models.AppSettings;
import com.ruslanlyalko.wl.data.models.CheckBlocked;
import com.ruslanlyalko.wl.data.models.CheckDate;
import com.ruslanlyalko.wl.data.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;

import static com.ruslanlyalko.wl.data.Config.DB_SETTINGS;
import static com.ruslanlyalko.wl.data.Config.DB_SETTINGS_GENERAL;
import static com.ruslanlyalko.wl.data.Config.DB_USERS;
import static com.ruslanlyalko.wl.data.Config.FIELD_DEFAULT_WORKING_TIME;
import static com.ruslanlyalko.wl.data.Config.FIELD_IS_NIGHT_MODE;
import static com.ruslanlyalko.wl.data.Config.FIELD_IS_OLD_STYLE_CALENDAR;
import static com.ruslanlyalko.wl.data.Config.FIELD_NAME;
import static com.ruslanlyalko.wl.data.Config.FIELD_REMIND_ME_AT;
import static com.ruslanlyalko.wl.data.Config.FIELD_TOKEN;
import static com.ruslanlyalko.wl.data.Config.FIELD_TOKENS;
import static com.ruslanlyalko.wl.data.Config.FIELD_VERSION;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class DataManagerFirestoreImpl implements DataManager {

    private static final String TAG = "DataManager";
    private static DataManagerFirestoreImpl mInstance;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseFunctions mFunctions;
    private MutableLiveData<User> mCurrentUserLiveData;
    private MutableLiveData<List<User>> mAllUsersListLiveData;
    private MutableLiveData<AppSettings> mSettingsLiveData;

    private DataManagerFirestoreImpl() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
    }

    public static DataManager newInstance() {
        if (mInstance == null)
            mInstance = new DataManagerFirestoreImpl();
        return mInstance;
    }

    @Override
    public Task<Void> saveUser(final User user) {
        if (user.getKey() == null) {
            throw new RuntimeException("user can't be empty");
        }
        return mDatabase.collection(DB_USERS)
                .document(user.getKey())
                .update(user.toMap());
    }

    @Override
    public MutableLiveData<User> getMyUser() {
        if (mCurrentUserLiveData != null) {
            return mCurrentUserLiveData;
        }
        mCurrentUserLiveData = new MutableLiveData<>();
        String key = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (key == null) {
            Log.w(TAG, "getMyUser: user is not logged in");
            return mCurrentUserLiveData;
        }
        mDatabase.collection(DB_USERS)
                .document(key)
                .addSnapshotListener((documentSnapshot, e) -> {
                    Log.d(TAG, "getMyUser:onDataChange, Key:" + key);
                    if (e == null && mCurrentUserLiveData != null && documentSnapshot != null) {
                        mCurrentUserLiveData.postValue(documentSnapshot.toObject(User.class));
                    }
                });
        return mCurrentUserLiveData;
    }

    @Override
    public MutableLiveData<User> getUser(final String key) {
        final MutableLiveData<User> userLiveData = new MutableLiveData<>();
        if (TextUtils.isEmpty(key)) {
            Log.w(TAG, "getUser has wrong argument");
            return userLiveData;
        }
        mDatabase.collection(DB_USERS)
                .document(key)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e == null && mCurrentUserLiveData != null && documentSnapshot != null) {
                        mCurrentUserLiveData.postValue(documentSnapshot.toObject(User.class));
                    }
                });
        return userLiveData;
    }

    @Override
    public MutableLiveData<List<User>> getAllUsers() {
        if (mAllUsersListLiveData != null) return mAllUsersListLiveData;
        mAllUsersListLiveData = new MutableLiveData<>();
        mDatabase.collection(DB_USERS)
                .orderBy(FIELD_NAME)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    Log.d(TAG, "getAllUsers:onDataChange");
                    if (e == null && mCurrentUserLiveData != null && queryDocumentSnapshots != null) {
                        List<User> list = new ArrayList<>();
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            list.add(snap.toObject(User.class));
                        }
                        mAllUsersListLiveData.postValue(list);
                    }
                });
        return mAllUsersListLiveData;
    }

    @Override
    public Task<Void> changePassword(final String newPassword) {
        if (mAuth.getCurrentUser() == null) return null;
        return mAuth.getCurrentUser().updatePassword(newPassword);
    }

    @Override
    public void updateRemindMeAt(final String remindMeAt) {
        if (mAuth.getCurrentUser() == null) return;
        mDatabase.collection(DB_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .update(FIELD_REMIND_ME_AT, remindMeAt);
    }

    @Override
    public void updateDefaultWorkingTime(final int defaultWorkingTime) {
        if (mAuth.getCurrentUser() == null) return;
        mDatabase.collection(DB_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .update(FIELD_DEFAULT_WORKING_TIME, defaultWorkingTime);
    }

    @Override
    public void updateOldStyleCalendar(final boolean isEnabled) {
        if (mAuth.getCurrentUser() == null) return;
        mDatabase.collection(DB_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .update(FIELD_IS_OLD_STYLE_CALENDAR, isEnabled);
    }

    @Override
    public void updateVersion() {
        if (mAuth.getCurrentUser() == null) return;
        mDatabase.collection(DB_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .update(FIELD_VERSION, BuildConfig.VERSION_NAME);
    }

    @Override
    public void updateNightMode(final boolean isNightMode) {
        if (mAuth.getCurrentUser() == null) return;
        mDatabase.collection(DB_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .update(FIELD_IS_NIGHT_MODE, isNightMode);
    }

    @Override
    public void updateToken() {
        if (mAuth.getCurrentUser() == null) return;
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null && !TextUtils.isEmpty(token))
            mDatabase.collection(DB_USERS)
                    .document(mAuth.getCurrentUser().getUid())
                    .update(FIELD_TOKENS, token);
    }

    @Override
    public void logout() {
        if (mAuth.getCurrentUser() == null) return;
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null && !TextUtils.isEmpty(token))
            mDatabase.collection(DB_USERS)
                    .document(mAuth.getCurrentUser().getUid())
                    .update(FIELD_TOKENS, null);
        mDatabase.collection(DB_USERS)
                .document(mAuth.getCurrentUser().getUid())
                .update(FIELD_TOKEN, null);
        mCurrentUserLiveData = null;
        mAuth.signOut();
    }

    @Override
    public void clearCache() {
        mCurrentUserLiveData = null;
    }

    @Override
    public Task<CheckBlocked> isBlocked() {
        Map<String, Object> data = new HashMap<>();
        return mFunctions
                .getHttpsCallable("isBlocked")
                .call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if (task.isSuccessful()) {
                        try {
                            return new CheckBlocked(task.getResult().getData());
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return null;
                });
    }

    @Override
    public Task<CheckDate> isRightDate() {
        Map<String, Object> data = new HashMap<>();
        data.put("date", new Date().getTime());
        return mFunctions
                .getHttpsCallable("isRightDate")
                .call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if (task.isSuccessful()) {
                        try {
                            return new CheckDate(task.getResult().getData());
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return null;
                });
    }

    @Override
    public MutableLiveData<AppSettings> getSettings() {
        if (mSettingsLiveData != null) return mSettingsLiveData;
        mSettingsLiveData = new MutableLiveData<>();
        mDatabase.collection(DB_SETTINGS)
                .document(DB_SETTINGS_GENERAL)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e == null && mCurrentUserLiveData != null && documentSnapshot != null) {
                        AppSettings value = documentSnapshot.toObject(AppSettings.class);
                        if (value == null)
                            value = new AppSettings();
                        if (mSettingsLiveData != null)
                            mSettingsLiveData.postValue(value);
                    }
                });
        return mSettingsLiveData;
    }

    @Override
    public Task<Void> setSettings(final AppSettings settings) {
        return mDatabase.collection(DB_SETTINGS).document(DB_SETTINGS_GENERAL).update(settings.toMap());
    }
}
