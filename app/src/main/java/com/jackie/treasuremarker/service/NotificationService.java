package com.jackie.treasuremarker.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.jackie.treasuremarker.R;

import java.util.Date;
import java.util.LinkedList;

public class NotificationService extends Service {
    private NotificationManager manager;
    private LinkedList<NotificationData> list = new LinkedList<>();
    boolean connected = false;
    final static String TAG = "NotificationService";

    private static class NotificationData {
        private String title;
        private Date date;

        public NotificationData() {}

        public NotificationData(String title, Date date) {
            this.title = title;
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public class NotificationBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }

        public void appendNotification(String text, Date date) {
            list.add(new NotificationData(text, date));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("alarm", "Default", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);

        new Thread(() -> {
            connected = true;
            while (connected) {
//                Log.i(TAG, "Date check");
                Date curDate = new Date();
                for (NotificationData i : list) {
                    if (i.getDate().before(curDate)) {
                        Log.i(TAG, "Date arrived");
                        Notification notification = new NotificationCompat.Builder(this, "alarm")
                                .setAutoCancel(true)
                                .setContentTitle("Received alarm")
                                .setContentText(i.getTitle())
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .build();
                        manager.notify(1, notification);
                        list.remove(i);
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.i(TAG, "Notification service created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new NotificationBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connected = false;
        list = null;
    }
}
