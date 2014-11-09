package ar.com.martinrevert.argenteam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;


public class EmuleRequest extends BaseActivity {

	public String session;
	private String webserver;
	private String puerto;
	private String pass;
    private String passed;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(EmuleRequest.this);
		SharedPreferences emuleserver = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		webserver = emuleserver.getString("webserver", "");
		puerto = emuleserver.getString("puerto", "");
		pass = emuleserver.getString("password", "");

		passed = getIntent().getStringExtra("passed");
		
		String message = "http://" + webserver + ":" + puerto + "/?";
		Log.v("DATOS SERVER", "server " + webserver + " puerto " + puerto
				+ " password " + pass);
		Log.v("MESAGGE", message);
		
		if (isOnline() && !webserver.equals("") && !puerto.equals("") && !pass.equals("")) {
			new RequestTask().execute(message);
		} else {
			vibrateToast(R.string.sinemule);
			finish();
			
		}
		
	}

	class RequestTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage(EmuleRequest.this.getResources().getString(R.string.sended2k));
			dialog.show();
		}

		@Override
		protected String doInBackground(String... uri) {

			Document doc;
			int numtries = 3;
			while(true){
			try {
				doc = Jsoup.connect(uri[0]).data("w", "password")
						.data("p", pass).timeout(10000).post();
				Element data = doc.select("a[href$=transfer]").first();
				String previo = data.attr("href");

				session = previo.substring(0, previo.indexOf("&"));
				Log.v("SESSION", session + "  " + previo);

				
			} catch (IOException e) {
				// TODO Capturar excepción en enviar return amigable
				e.printStackTrace();
				
				if (--numtries == 0)
					try {
						throw e;
					} catch (IOException e1) {
						// TODO Capturar excepción en enviar return amigable
						e1.printStackTrace();
					}
				
				
			
			}
			return session;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result == null){
				vibrateToast(R.string.sinemuleiniciado);
				finish();
			}else{
			
			Log.v("EMULE", result);// Do anything with response..
                String ed2klink = "http://" + webserver + ":" + puerto + result
                        + "&w=transfer&ed2k=" + passed;

			new TransferFile().execute(ed2klink);
		}
	}
	}
	private class TransferFile extends AsyncTask<String, String, String> {
		URI uriposta;

		@Override
		protected String doInBackground(String... uribla) {
			URL url = null;
			try {
				url = new URL(uribla[0]);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				uriposta = new URI(url.getProtocol(), null, url.getHost(),
						url.getPort(), url.getPath(), url.getQuery(), null);

				Log.v("URIPOSTA", uriposta.toString());
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				response = httpclient.execute(new HttpGet(uriposta));
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
					Log.v("RESPONSE", responseString);
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				// TODO Handle problems..
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Handle problems..
				e.printStackTrace();
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			dialog.dismiss();
			vibrateToast(R.string.emulesendedok);
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        // TODO Hacer Logout de la sesión Emule
		}
}
