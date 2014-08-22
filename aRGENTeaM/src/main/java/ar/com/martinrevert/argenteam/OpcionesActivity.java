package ar.com.martinrevert.argenteam;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

public class OpcionesActivity extends PreferenceActivity {

    private static final int REQUEST_DIRECTORY = 0;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Implementar Fragment Preferences
		super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.opciones);
        Preference myPref = findPreference("myKey");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                Log.v("DIRECTORIO", "CLICK!");
                final Intent chooserIntent;
                chooserIntent = new Intent(OpcionesActivity.this, DirectoryChooserActivity.class);
                chooserIntent.putExtra(
                        DirectoryChooserActivity.EXTRA_NEW_DIR_NAME,
                        "mis_subtitulos");

                startActivityForResult(chooserIntent, REQUEST_DIRECTORY);

                return false;
            }
        });

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {

                handleDirectoryChoice(data
                        .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
            } else {

              // myPref.setSummary("");
            }
        }
    }

    private void handleDirectoryChoice(String stringExtra) {
        //myPref.setSummary(stringExtra);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
