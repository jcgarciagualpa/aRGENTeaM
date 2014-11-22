package ar.com.martinrevert.argenteam;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class BaseActivity extends ActionBarActivity {

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

    public String getYouTubeId(String url) {
        String id = null;
        String regExp = "/.*(?:youtu.be\\/|v\\/|u/\\w/|embed\\/|watch\\?.*&?v=)";
        Pattern compiledPattern = Pattern.compile(regExp);
        Matcher matcher = compiledPattern.matcher(url);
        if(matcher.find()){
            int start = matcher.end();
            id =  url.substring(start, start+11);
        }
      return id;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //ToDo inflar de acuerdo a la activity que se está usando
        inflater.inflate(R.menu.menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //   case R.id.search:
            //        onSearchRequested();
            //        return true;
            case R.id.share:
                Intent sharingIntent = new Intent(
                        android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=ar.com.martinrevert.argenteam";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        getResources().getString(R.string.lookthisapp));
                sharingIntent
                        .putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent,
                        getResources().getString(R.string.sharefriend)));
                return true;

            case R.id.settings:

                startActivity(new Intent(this, OpcionesActivity.class));

                return true;

            case R.id.about:
                String version = "";
                try {
                    version = getPackageManager().getPackageInfo(getPackageName(),
                            0).versionName;
                } catch (PackageManager.NameNotFoundException e) {

                    e.printStackTrace();
                }

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("aRGENTeaM for Android " + version);
                builder1.setIcon(R.drawable.stubportrait);
                //ToDo Traducir esto
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

}
