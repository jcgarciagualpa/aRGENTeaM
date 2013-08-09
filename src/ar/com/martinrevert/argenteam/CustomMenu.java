package ar.com.martinrevert.argenteam;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class CustomMenu extends FragmentActivity {
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	private String version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
		if (!(this instanceof Main)){
			ActionBar actionbar;
            actionbar = this.getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.search:
			 onSearchRequested();
		
			return true;
		case R.id.share:
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = "https://play.google.com/store/apps/details?id=ar.com.martinrevert.argenteam";
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Subject Here");
			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent,
					"Compartir URL via"));
			return true;

		case R.id.settings:

			startActivity(new Intent(this, OpcionesActivity.class));

			return true;

		case R.id.about:
			try {
				version = getPackageManager().getPackageInfo(getPackageName(),
						0).versionName;
			} catch (NameNotFoundException e) {
				
				e.printStackTrace();
			}

			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("aRGENTeaM for Android " + version);
			builder1.setIcon(R.drawable.stubportrait);
			Spannable tex = new SpannableString("Esta aplicación es freeware provisto \"as is\".\n\n\nmartinrevert@gmail.com");
			Linkify.addLinks(tex, Linkify.EMAIL_ADDRESSES);
			builder1.setMessage(tex);

			builder1.setNegativeButton("Aceptar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();

						}
					});
			AlertDialog alert1 = builder1.create();
			alert1.show();
			return true;
			
		case android.R.id.home:
			Intent intent = new Intent(this, Main.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true; 

		default:
			return super.onOptionsItemSelected(item);

		}
		
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();

	}

	public Void vibrateToast(String message) {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		int duration = Toast.LENGTH_LONG;
		v.vibrate(300);
		Toast toast = Toast.makeText(getBaseContext(), message, duration);
		toast.show();
		return null;
	}
	
	public boolean isP2P(String url){
			
	    PackageManager manager = getPackageManager();
	    Intent intent = new Intent();
	    intent.setAction(Intent.ACTION_VIEW);
	    intent.addCategory(Intent.CATEGORY_BROWSABLE);
	    // NOTE: Provide some data to help the Intent resolver
	    intent.setData(Uri.parse(url));
	    // Query for all activities that match my filter and request that the filter used
	    //  to match is returned in the ResolveInfo
	    boolean p2 = false;
	    List<ResolveInfo> infos = manager.queryIntentActivities (intent,
	                                   PackageManager.GET_RESOLVED_FILTER);
	    for (ResolveInfo info : infos) {
	        
	        IntentFilter filter = info.filter;
	        if (filter != null && filter.hasAction(Intent.ACTION_VIEW) &&
	                  filter.hasCategory(Intent.CATEGORY_BROWSABLE)) {
	            // This activity resolves my Intent with the filter I'm looking for
	            
	            Log.v("APP", info.toString());
	            p2 = true;
	        }else{
	        	p2 = false;
	        	
	        }
	    }
		return p2;
		
		
		
		
	}


}
