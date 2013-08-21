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
	private String webserver;
	private String puerto;
	private String usuario;
	private String password;
	private String passed;
	private String message;
	private String auth;
	Document doc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		dialog = new ProgressDialog(this);
		SharedPreferences torrentserver = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		webserver = torrentserver.getString("webservertorrent", "");
		puerto = torrentserver.getString("puertotorrent", "");
		usuario = torrentserver.getString("usuariotorrent", "");
		password = torrentserver.getString("passwordtorrent", "");

		passed = getIntent().getStringExtra("passed");

		byte[] encodedLogin = (usuario + ":" + password).getBytes();
		auth = "Basic " + Base64.encodeBytes(encodedLogin);

		// En caso de dejar de soportar cambiar a lib android.util.BASE64 y
		// habilitar esto
		// auth = "Basic " + Base64.encodeToString(encodedLogin, 0);

		message = "http://" + webserver + ":" + puerto
				+ "/gui/?action=add-url&s=" + passed;

		if (isOnline() && webserver != "" && puerto != ""
				&& usuario != "" && password != "") {
			new RequestTask().execute(message);
		} else {
			vibrateToast("Sin internet o tienes mal configurados parámetros de uTorrent");
			finish();

		}

	}

	private class RequestTask extends AsyncTask<String, String, String> {

		private String response;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			dialog.setMessage("Enviando link a tu uTorrent...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... uri) {

			int numtries = 3;
			while (true) {
				try {

					doc = Jsoup.connect(uri[0]).timeout(60000)
							.header("Authorization", auth).get();

					response = doc.body().text();

					Log.v("TORRENT", response);

				} catch (IOException e) {

					e.printStackTrace();

					if (--numtries == 0)
						try {
							throw e;
						} catch (IOException e1) {

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

			vibrateToast("Link torrent enviado correctamente a tu uTorrent");

			finish();

		}

	}

}