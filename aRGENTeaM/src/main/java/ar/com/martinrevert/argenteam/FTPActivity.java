package ar.com.martinrevert.argenteam;

import java.io.File;
import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

public class FTPActivity extends CustomMenu {

    private ListView lista;
    LazyAdapterSubs adapter;

    public ArrayList<String> ruta = new ArrayList<String>();
    public ArrayList<String> sub = new ArrayList<String>();


    public String getPath() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString("path", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.missubtitulos);
        lista = (ListView) findViewById(R.id.listSubs);

        new GetSubsSDcard().execute();

    }

    public class GetSubsSDcard extends AsyncTask<Void, Void, Void> {

        File[] files;

        File dirPath = new File(getPath());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {


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
