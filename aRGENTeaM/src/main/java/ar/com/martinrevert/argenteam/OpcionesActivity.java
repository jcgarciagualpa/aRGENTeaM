package ar.com.martinrevert.argenteam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

import java.util.Iterator;
import java.util.Set;


public class OpcionesActivity extends PreferenceActivity {

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

    private static final int REQUEST_DIRECTORY = 0;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        // TODO Implementar Fragment Preferences
        super.onCreate(savedInstanceState);
       addPreferencesFromResource(R.xml.opciones);

        Preference myPref = findPreference("pathsubs");
        myPref.setSummary(getPath());
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                final Intent chooserIntent;
                chooserIntent = new Intent(OpcionesActivity.this, DirectoryChooserActivity.class);
                chooserIntent.putExtra(
                        DirectoryChooserActivity.EXTRA_NEW_DIR_NAME,
                        "mis_subs");
                chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_INITIAL_DIRECTORY, getPath());
                startActivityForResult(chooserIntent, REQUEST_DIRECTORY);

                return false;

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("resultCode", String.valueOf(resultCode));
        Log.v("requestCode", String.valueOf(requestCode));
        Log.v("data", String.valueOf(data));

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {

                dumpIntent(data);

                handleDirectoryChoice(data
                        .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));



            }
        }
    }

    public static void dumpIntent(Intent i){

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e("DUMP","Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e("KEY","[" + key + "=" + bundle.get(key)+"]");
            }
            Log.e("END","Dumping Intent end");
        }
    }



    private void handleDirectoryChoice(String stringExtra) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("path", stringExtra);
        editor.apply();

        Preference myPref = findPreference("pathsubs");
        myPref.setSummary(stringExtra);
        getPath();


    }

    public String getPath(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("path","");
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
