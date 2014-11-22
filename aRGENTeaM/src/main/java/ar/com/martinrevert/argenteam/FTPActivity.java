package ar.com.martinrevert.argenteam;

import java.io.File;
import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

public class FTPActivity extends BaseActivity {

    private ListView lista;
    private LazyAdapterSubs adapter;
    private Toolbar toolbar;
    private ArrayList<String> ruta = new ArrayList<String>();
    private ArrayList<String> sub = new ArrayList<String>();


    public String getPath() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString("path", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.missubtitulos);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My subtitles");


        lista = (ListView) findViewById(R.id.listSubs);
        String dirRuta = getPath();
        Log.v("dirRuta", dirRuta);
        if (dirRuta.equals("")){
            vibrateToast(R.string.nodirsubs);
            finish();
        }


        new GetSubsSDcard().execute();

    }

    public class GetSubsSDcard extends AsyncTask<Void, Void, Void> {

        File[] files;
        String dirRuta = getPath();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {


            File dirPath = new File(dirRuta);
            files = dirPath.listFiles();

            if (files != null) {

                for (File file : files) {
                    String name = file.getName();
                    String path = file.getAbsolutePath();
                    Log.v("FILE", name);
                    if (name.endsWith("srt")) {
                        sub.add(name);
                        ruta.add(path);
                    }
                }

                adapter = new LazyAdapterSubs(FTPActivity.this, sub, ruta);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lista.setAdapter(adapter);
        }

    }

}
