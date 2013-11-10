package ar.com.martinrevert.argenteam;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class OpcionesActivity extends PreferenceActivity {



    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Implementar Fragment Preferences
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.opciones);

	}

    @Override
    protected void onPause() {
        super.onPause();

    }

}
