package com.ruslanlyalko.wl.presentation.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ruslanlyalko.wl.R;
import com.ruslanlyalko.wl.data.DataManagerFirestoreImpl;
import com.ruslanlyalko.wl.presentation.ui.splash.SplashActivity;

import androidx.core.content.ContextCompat;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "PushService";
    private static final String CHANEL_ID = "a_default_id";
    private static final String CHANEL_NAME = "Default";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final int NOTIFICATION_ID = 1;

    public static void removeNotifications(Context context) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancelAll();
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null && remoteMessage.getData().containsKey(KEY_TITLE) && remoteMessage.getData().containsKey(KEY_BODY)) {
            Log.d(TAG, remoteMessage.getData().toString());
            showNotification(this, remoteMessage.getData().get(KEY_TITLE), remoteMessage.getData().get(KEY_BODY));
        } else if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getTitle() != null && remoteMessage.getNotification().getBody() != null) {
            Log.d(TAG, remoteMessage.getNotification().getBody());
            showNotification(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String token) {
        DataManagerFirestoreImpl.newInstance().updateToken();
        Log.d(TAG, "Refreshed token: " + token);
    }

    private void showNotification(Context context, String title, String body) {
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.workload_notification);
        //        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;
        final Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANEL_ID,
                    CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            channel.setSound(soundUri, att);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new Notification.Builder(context, CHANEL_ID);
            notificationBuilder.setChannelId(CHANEL_ID);
            notificationManager.getNotificationChannel(CHANEL_ID).setSound(soundUri, att);
        } else {
            notificationBuilder = new Notification.Builder(context);
        }
        notificationBuilder
                .setSmallIcon(R.drawable.ic_stat_main)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(body)
                .setTicker(body)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(soundUri)
                .setAutoCancel(true);
        final PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
                SplashActivity.getLaunchIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
        if (pIntent != null) {
            notificationBuilder.setContentIntent(pIntent);
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
