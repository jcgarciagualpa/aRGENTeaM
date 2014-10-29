package ar.com.martinrevert.argenteam;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class CustomMenu extends ActionBarActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    public Void vibrateToast(int message) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        int duration = Toast.LENGTH_LONG;
        v.vibrate(300);
        Toast toast = Toast.makeText(getBaseContext(), message, duration);
        toast.show();
        return null;
    }

    public boolean isP2P(String url) {

        PackageManager manager = getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        // NOTE: Provide some data to help the Intent resolver
        intent.setData(Uri.parse(url));
        // Query for all activities that match my filter and request that the filter used
        //  to match is returned in the ResolveInfo
        boolean p2 = false;
        List<ResolveInfo> infos = manager.queryIntentActivities(intent,
                PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo info : infos) {

            IntentFilter filter = info.filter;
            if (filter != null && filter.hasAction(Intent.ACTION_VIEW) &&
                    filter.hasCategory(Intent.CATEGORY_BROWSABLE)) {
                // This activity resolves my Intent with the filter I'm looking for

                Log.v("APP", info.toString());
                p2 = true;
            } else {
                p2 = false;

            }
        }
        return p2;


    }


}
