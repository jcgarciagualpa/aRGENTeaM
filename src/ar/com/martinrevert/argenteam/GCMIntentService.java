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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.app.ServerUtilities;

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
        // String message = getString(R.string.gcm_message);
        String message = intent.getExtras().getString("message");
        String tipo = "movie";
        String urlimagen = "http://www.argenteam.net/resources/covers/thumbs/9f9e86d6776be00e237500c07c5a0473.jpg";
        String urlarticulo = "http://www.argenteam.net/movie/71415/Dead.Man.Down.%282013%29";

        //displayMessage(context, message);
        // notifies user
        generarNotification(context, message, urlimagen, urlarticulo, tipo);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        String tipo = "";
        String urlimagen = "";
        String urlarticulo = "";
        //displayMessage(context, message);
        // notifies user
        generarNotification(context, message, urlimagen, urlarticulo, tipo);
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

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generarNotification(Context context, String message, String URLimagen, String URLarticulo, String tipo) {
        int icon = R.drawable.ic_stat_ic_argenteam_gcm;
        String eol = System.getProperty("line.separator");
        message = message.replace("regex", eol);

        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        boolean vib = preferencias.getBoolean("vibraoff", false);
        boolean son = preferencias.getBoolean("audioff", false);
        String ringtone = preferencias.getString("prefRingtone", "");
        String ticker = "Nueva " + tipo + " en aRGENTeaM";



        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        int dash = 500;     // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200;    // Length of Gap Between dots/dashes
        int medium_gap = 500;   // Length of Gap Between Letters

        long[] pattern = {
                0,  // Start immediately
                dash, short_gap, dash, short_gap, dash, // o
                medium_gap
        };

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, Main.class);
        // set intent so it does not start a new activity
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
        //        Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);


        Notification myNotification;
        myNotification = new NotificationCompat.Builder(context)
                .setContentTitle("Exercise of Notification!")
                .setContentText("Content text")
                .setSubText("subtexto")
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setSound(Uri.parse(ringtone))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle())
                .setSmallIcon(R.drawable.ic_stat_ic_argenteam_gcm)
                .build();


        if (vib) {
            v.vibrate(pattern, -1);
        }

        if (son) {

            notificationManager.notify(0, myNotification);
        }

    }

}
