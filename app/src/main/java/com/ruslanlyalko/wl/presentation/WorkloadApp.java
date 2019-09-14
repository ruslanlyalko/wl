package com.ruslanlyalko.wl.presentation;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by Ruslan Lyalko
 * on 01.08.2019.
 */
public class WorkloadApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
