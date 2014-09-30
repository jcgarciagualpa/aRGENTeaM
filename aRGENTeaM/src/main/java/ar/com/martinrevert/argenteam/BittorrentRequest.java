package ar.com.martinrevert.argenteam;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.ProgressDialog;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;

import android.util.Log;

public class BittorrentRequest extends CustomMenu {

    ProgressDialog dialog;
    private String auth;
    Document doc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(this);
        SharedPreferences torrentserver = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String webserver = torrentserver.getString("webservertorrent", "");
        String puerto = torrentserver.getString("puertotorrent", "");
        String usuario = torrentserver.getString("usuariotorrent", "");
        String password = torrentserver.getString("passwordtorrent", "");

        String passed = getIntent().getStringExtra("passed");

        byte[] encodedLogin = (usuario + ":" + password).getBytes();
        auth = "Basic " + Base64.encodeBytes(encodedLogin);

        // En caso de dejar de soportar cambiar a lib android.util.BASE64 y
        // habilitar esto
        // auth = "Basic " + Base64.encodeToString(encodedLogin, 0);

        String message = "http://" + webserver + ":" + puerto
                + "/gui/?action=add-url&s=" + passed;

        if (isOnline() && !webserver.equals("") && !puerto.equals("")
                && !usuario.equals("") && !password.equals("")) {
            new RequestTask().execute(message);
        } else {
            vibrateToast(R.string.sintorrent);
            finish();

        }

    }

    private class RequestTask extends AsyncTask<String, String, String> {

        private String response;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog.setMessage(BittorrentRequest.this.getResources().getString(R.string.sendtorrent));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... uri) {

            int numtries = 3;
            while (true) {
                try {

                    doc = Jsoup.connect(uri[0]).timeout(10000)
                            .header("Authorization", auth).get();

                    response = doc.body().text();
                    //TODO Hacer algo con la respuesta para confirmar éxito o no
                    Log.v("TORRENT", response);

                } catch (IOException e) {

                    e.printStackTrace();

                    if (--numtries == 0)
                        try {
                            throw e;
                        } catch (IOException e1) {
                            //TODO Capturar excepción y usar return en caso de problemas
                            e1.printStackTrace();
                        }

                }
                return response;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            dialog.dismiss();

            vibrateToast(R.string.torrentsendedok);

            finish();

        }

    }

}
