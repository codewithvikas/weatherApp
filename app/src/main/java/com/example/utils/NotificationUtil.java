package com.example.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.aac.network.WeatherIntentService;
import com.example.aac.network.WeatherTasks;
import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;

public class NotificationUtil {

    private static final int WEATHER_LIST_PENDING_INTENT_ID = 34523;

    private static final int IGNORE_PENDING_INTENT_ID = 12345;
    private static final int SHARE_PENDING_INTENT_ID = 223311;

    private static final int WEATHER_LIST_NOTIFICATION_ID = 12567;
    private static final String WEATHER_LIST_NOTIFICATION_CHANNEL_ID = "weather-list-notification-channel";


    private static NotificationCompat.Action ignoreAction(Context context){
        Intent ignoreIntent = new Intent(context, WeatherIntentService.class);
        ignoreIntent.setAction(WeatherTasks.ACTION_CLEAR_NOTIFICATION);

        PendingIntent ignorePendingIntent = PendingIntent.getService(
                context,
                IGNORE_PENDING_INTENT_ID,
                ignoreIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action(
                R.drawable.ic_close_24,
                "No Thanks",
                ignorePendingIntent
        );
        return action;
    }

    private static NotificationCompat.Action shareAction(Context context){
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,"This is something i want to share with you !!");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent,"Select An app to share");

        PendingIntent sharePendingIntent = PendingIntent.getActivity(
                context,
                SHARE_PENDING_INTENT_ID,
                shareIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Action shareAction = new NotificationCompat.Action(
                R.drawable.ic_share_24,
                "Share Please",
                sharePendingIntent
        );
        return shareAction;
    }


    public static void clearAllNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
    public static void remindUserWeatherListUpdate(Context context){
        //Create Notification
        //Create Notification channel
        // Display Notification

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    WEATHER_LIST_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(context,WEATHER_LIST_NOTIFICATION_CHANNEL_ID)
                                    .setColor(ContextCompat.getColor(context,R.color.design_default_color_primary))
                                    .setSmallIcon(R.drawable.ic_cloudy)
                                    .setLargeIcon(largeIcon(context))
                                    .setContentTitle(context.getString(R.string.weather_notification_title))
                                    .setContentText(context.getString(R.string.weather_notification_massage))
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                            context.getString(R.string.weather_notification_body)))
                                    .setDefaults(Notification.DEFAULT_VIBRATE)
                                    .setContentIntent(contentIntent(context))
                                    .addAction(ignoreAction(context))
                                    .addAction(shareAction(context))
                                    .setAutoCancel(true);

        notificationManager.notify(WEATHER_LIST_NOTIFICATION_ID,notificationBuilder.build());


    }
    private static PendingIntent contentIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                WEATHER_LIST_PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT );
    }
    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.art_clear);
        return largeIcon;
    }
}
