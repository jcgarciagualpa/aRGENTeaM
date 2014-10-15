/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ar.com.martinrevert.argenteam;

import android.app.Notification;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = "GcmIntentService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String message;
        String tipo;
        String urlimagen;
        String urlarticulo;
        String fecha;

        Context context = getApplicationContext();

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                generarNotification(context,"Error: " + extras.toString(),"","","","");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                generarNotification(context, "Deleted messages on server: " + extras.toString(),"","","","");
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Aqui obtengo los datos del mensaje que viene en el Intent.
                message = intent.getExtras().getString("message");
                tipo = intent.getExtras().getString("tipo");
                urlimagen = intent.getExtras().getString("urlimagen");
                urlarticulo = intent.getExtras().getString("urlarticulo");
                fecha = intent.getExtras().getString("fecha");
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                generarNotification(context, message, urlimagen, urlarticulo, tipo, fecha);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public Bitmap getRemoteImage(final String aURL, String tipo) {
        try {
            if (aURL.isEmpty()){
                if(tipo.equals("movie")) {
                    return BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.stubportrait);
                }
                else {

                    return BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.stublandscape);

                }
            }
            URL imagelink = new URL(aURL);
            final URLConnection conn = imagelink.openConnection();
            conn.connect();
            final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            BitmapFactory.Options options = new BitmapFactory.Options();
            final Bitmap scaledBitmap = BitmapFactory.decodeStream(bis, null, options);
            bis.close();
            return scaledBitmap;
        } catch (IOException e) {
                        e.printStackTrace();

        }
    return null;
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generarNotification(Context context, String message, String urlimagen, String urlarticulo, String tipo, String fecha) {
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        boolean vib = preferencias.getBoolean("vibraoff", false);
        boolean movieoff = preferencias.getBoolean("movieoff", false);
        boolean tvoff = preferencias.getBoolean("tvoff", false);
        String ringmovie = preferencias.getString("prefRingtonemovie", "");
        String ringtv = preferencias.getString("prefRingtonetv", "");
       //Todo traducir ticker
       // String ticker = "Nuevo subtítulo " + tipo + " en aRGENTeaM";
       String format = getResources().getString(R.string.ticker);
       String ticker = String.format(format,tipo);

        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(100);

        Bitmap bitmap = getRemoteImage(urlimagen, tipo);


        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        int dash = 500;
        int short_gap = 200;


        long[] pattern = {
                0,  // Start immediately
                dash, short_gap, dash, short_gap, dash
        };


        Intent notificationIntent;
        String ringtone;
        int ledlight;

        if (tipo.equalsIgnoreCase("Movie")) {
            ringtone = ringmovie;
            notificationIntent = new Intent(context, Peli.class);
            ledlight = preferencias.getInt("ledMovie", 0);

        } else {
            notificationIntent = new Intent(context, Tv.class);
            ringtone = ringtv;
            ledlight = preferencias.getInt("ledTV", 0);
            System.out.println(ledlight);
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("passed", urlarticulo);

        PendingIntent pendingIntent;

        pendingIntent = PendingIntent.getActivity(context, randomInt, notificationIntent, 0);

        Notification myNotification;
        myNotification = new NotificationCompat.Builder(context)
                .setPriority(1)
                .setContentTitle(message)
                .setTicker(ticker)
                .setLights(ledlight, 300, 300)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setSound(Uri.parse(ringtone))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_ic_argenteam_gcm)
                .build();



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            RemoteViews views;
            views = new RemoteViews(getPackageName(), R.layout.custom_notification);
            views.setImageViewBitmap(R.id.big_picture, bitmap);
            views.setImageViewBitmap(R.id.big_icon, BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_ic_argenteam_gcm));
            views.setTextViewText(R.id.title, message);
            myNotification.bigContentView = views;
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (movieoff && tipo.equalsIgnoreCase("Movie")) {
            if (vib) {
                v.vibrate(pattern, -1);
            }
            notificationManager.notify(randomInt, myNotification);

        }

        if (tvoff && tipo.equalsIgnoreCase("Serie TV")) {
            if (vib) {
                v.vibrate(pattern, -1);
            }
            notificationManager.notify(randomInt, myNotification);

        }

    }
}