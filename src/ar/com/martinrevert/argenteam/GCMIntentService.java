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

import static com.google.android.gcm.app.CommonUtilities.SENDER_ID;

import ar.com.martinrevert.argenteam.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.app.ServerUtilities;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        // displayMessage(context, getString(R.string.gcm_registered));
        ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");

        String message;
        String tipo;
        String urlimagen;
        String urlarticulo;
        String fecha;

        message = intent.getExtras().getString("message");
        tipo = intent.getExtras().getString("tipo");
        urlimagen = intent.getExtras().getString("urlimagen");
        urlarticulo = intent.getExtras().getString("urlarticulo");
        fecha = intent.getExtras().getString("fecha");


        //displayMessage(context, message);
        // notifies user
        generarNotification(context, message, urlimagen, urlarticulo, tipo, fecha);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        String tipo = "";
        String urlimagen = "";
        String urlarticulo = "";
        String fecha = "";
        //displayMessage(context, message);
        // notifies user
        generarNotification(context, message, urlimagen, urlarticulo, tipo, fecha);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        //  displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_recoverable_error,
        //  errorId));
        return super.onRecoverableError(context, errorId);
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public Bitmap getRemoteImage(final String aURL, String tipo) {
        try {
            URL imagelink = new URL(aURL);
            final URLConnection conn = imagelink.openConnection();
            conn.connect();
            final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            BitmapFactory.Options options = new BitmapFactory.Options();
            final Bitmap scaledBitmap = BitmapFactory.decodeStream(bis, null, options);
            bis.close();
            /*
            int width = bm.getWidth();
            int height = bm.getHeight();
            int boundingh;
            int boundingw;
            float xScale;
            float yScale;

           if (tipo.equalsIgnoreCase("Movie")){
               boundingh = dpToPx(256);
               yScale = boundingh / height;
              // boundingw = dpToPx(Math.round(width * yScale));
              // xScale = boundingw / width;
               xScale = yScale;
           }
            else
           {
               boundingh = dpToPx(256);
              // boundingw = dpToPx(300);

               yScale = boundingh / height;
               xScale = yScale;
           }



            Matrix matrix = new Matrix();
            matrix.postScale(xScale, yScale);

            Bitmap scaledBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

           float ancho = scaledBitmap.getWidth(); // re-use
           float alto = scaledBitmap.getHeight(); // re-use

            Log.v("Medidas", "Ancho: "+ancho+"Alto: "+alto);*/

            return scaledBitmap;
        } catch (IOException e) {
            // ToDo Mostrar imagen generica si falla el request

        }
        return null;
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generarNotification(Context context, String message, String urlimagen, String urlarticulo, String tipo, String fecha) {
        int icon = R.drawable.ic_stat_ic_argenteam_gcm;
        // String eol = System.getProperty("line.separator");
        // message = message.replace("regex", eol);

        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        boolean vib = preferencias.getBoolean("vibraoff", false);
        boolean movieoff = preferencias.getBoolean("movieoff", false);
        boolean tvoff = preferencias.getBoolean("tvoff", false);
        String ringmovie = preferencias.getString("prefRingtonemovie", "");
        String ringtv = preferencias.getString("prefRingtonetv", "");
        String ticker = "Nuevo subtítulo " + tipo + " en aRGENTeaM";

        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(100);

        Bitmap bitmap = getRemoteImage(urlimagen, tipo);


        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        int dash = 500;     // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200;    // Length of Gap


        long[] pattern = {
                0,  // Start immediately
                dash, short_gap, dash, short_gap, dash
        };


        Intent notificationIntent;
        String ringtone;
        if (tipo.equalsIgnoreCase("Movie")) {
            ringtone = ringmovie;
            notificationIntent = new Intent(context, Peli.class);

        } else {
            notificationIntent = new Intent(context, Tv.class);
            ringtone = ringtv;
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("passed", urlarticulo);

        PendingIntent pendingIntent;

        pendingIntent = PendingIntent.getActivity(context, randomInt, notificationIntent, 0);

        Notification myNotification;
        myNotification = new NotificationCompat.Builder(context)
                .setContentTitle(message)
                .setTicker(ticker)
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