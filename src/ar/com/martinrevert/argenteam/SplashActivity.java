package ar.com.martinrevert.argenteam;

import static com.google.android.gcm.app.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.google.android.gcm.app.CommonUtilities.SENDER_ID;
import static com.google.android.gcm.app.CommonUtilities.SERVER_URL;
import static com.google.android.gcm.app.CommonUtilities.EXTRA_MESSAGE;

import com.google.android.gcm.*;
import com.google.android.gcm.app.ServerUtilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity {

	private long ms = 0;
	private long splashTime = 4000;
	private boolean splashActive = true;
	private boolean paused = false;
	
	int sdkVersion = Build.VERSION.SDK_INT;

	
	
	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);
		
		if (sdkVersion != 7) {
				
		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		// setContentView(R.layout.main);
		// mDisplay = (TextView) findViewById(R.id.display);
	//	registerReceiver(mHandleMessageReceiver, new IntentFilter(
	//			DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				// mDisplay.append(getString(R.string.already_registered) +
				// "\n");
				Log.v("REGID", regId);
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilities.register(context,
								regId);

						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						if (!registered) {
							GCMRegistrar.unregister(context);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};//async

				mRegisterTask.execute(null, null, null);

			}//else
		}//else
		
	}// ZAFAR eclair
		
		Thread mythread = new Thread() {
			public void run() {
				try {
					while (splashActive && ms < splashTime) {
						if (!paused)
							ms = ms + 100;
						sleep(100);
					}
				} catch (Exception e) {
				} finally {
					Intent intent = new Intent(SplashActivity.this, Main.class);
					startActivity(intent);
				}
			}
		};
		mythread.start();
	}//oncreate
	
	  @Override
	    protected void onDestroy() {
		  
		  if (sdkVersion != 7) {
		  
	        if (mRegisterTask != null) {
	            mRegisterTask.cancel(true);
	        }
	  //      unregisterReceiver(mHandleMessageReceiver);
	        GCMRegistrar.onDestroy(getApplicationContext());
	        super.onDestroy();
	        
	    }else{
	    super.onDestroy();
	  }
	  }
	  
	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name)); // ojo con este string
		}
	}
/*
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			//mDisplay.append(newMessage + "\n");
		}

	};
	
*/
}
