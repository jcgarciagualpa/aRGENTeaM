package ar.com.martinrevert.argenteam;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class SearchableActivity extends Activity {

	public String response=null;
	public String test;
	public String pegaitems;
	public String[] pst;
	public String[] imag;
	public String[] titulo;
	public String[] tipo;
	public LazyAdapterBuscador adapter;
	ListView lista;
	int size = 0;

	private String query;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista);
		lista = (ListView) findViewById(R.id.listView1);

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

			query = intent.getStringExtra(SearchManager.QUERY);
			Log.v("QUERY", query);
			new DoMySearch().execute(query);

		}

	}

	private class DoMySearch extends AsyncTask<String, String, String> {
		ProgressDialog dialog = new ProgressDialog(SearchableActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Buscando '" + query + "'");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... palabras) {
			Document doc = null;
			int numtries = 3;
			while (true) {
				try {
					doc = Jsoup.connect("http://www.argenteam.net/search")
							.data("filter", palabras[0])
							.data("max","50")
							.get();

					if (doc.select("div.pack-list").text()
							.equals("No se encontraron coincidencias")) {

						Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(300);

						runOnUiThread(new Runnable() {
							public void run() {
								dialog.dismiss();
								int duration = Toast.LENGTH_LONG;
								Toast toast = Toast.makeText(getBaseContext(), "No se encontraron coincidencias", duration);
								toast.show();
								
								
								
								//onSearchRequested();
							}
							
						});
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finish();
					}//fin sin coincidencias

					Elements metalinks = doc.select("meta[property=og:type]");
					String meta;
					if (metalinks.isEmpty()) {
						meta = "NOT_FOUND";
					} else {
						String metatagcontent = metalinks.first().attr(
								"content");
						meta = metatagcontent;

						if (meta.equals("movie")) {
							
							runOnUiThread(new Runnable() {
								public void run() {
									dialog.dismiss();
								}
							});
											
							
							Elements uris = doc.select("meta[property=og:url");
							String URIpost = uris.first().attr("content");
							Log.v("URL", URIpost);
							Intent peliPage = new Intent(
									SearchableActivity.this, Peli.class);
							peliPage.putExtra("passed", URIpost);
							SearchableActivity.this.startActivityForResult(
									peliPage, 0);
							finish();
							
							

						} else {
							
							runOnUiThread(new Runnable() {
								public void run() {
									dialog.dismiss();
								}
							});
							
							Elements uris = doc.select("meta[property=og:url");
							String URIpost = uris.first().attr("content");
							Log.v("URL", URIpost);
							Intent peliPage = new Intent(
									SearchableActivity.this, TvSeasons.class);
							peliPage.putExtra("passed", URIpost);
							SearchableActivity.this.startActivityForResult(
									peliPage, 0);
							finish();
						}

					}
		
					Log.v("META", meta);

					Iterator<Element> sizemax = doc
							.select("div.pack-list > div.pack-list-item > div.search-item-desc > a > h1")
							.iterator();

					while (sizemax.hasNext()) {

						@SuppressWarnings("unused")
						Element rel = sizemax.next();

						size++;
					}

					titulo = new String[size];
					imag = new String[size];
					pst = new String[size];
					tipo = new String[size];

					Iterator<Element> releases = doc
							.select("div.pack-list > div.pack-list-item > div.search-item-desc > a > h1")
							.iterator();

					int counttit = 0;
					while (releases.hasNext()) {

						Element rel = releases.next();
						String release = rel.text();
						Log.v("RELESE", release);
						titulo[counttit] = release;
						counttit++;
					}

					Iterator<Element> images = doc
							.select("div.pack-list > div.pack-list-item > div.search-cover")
							.iterator();
					int countimg = 0;
					while (images.hasNext()) {

						Element img = images.next();

						if (img.select("img").first() == null) {
							imag[countimg] = "http://www.argenteam.net/images/header-background.gif";
						} else {
							String imageurl = img.select("img").first()
									.attr("src").toString();
							imag[countimg] = imageurl;
						}

						Log.v("IMAGEN", imag[countimg]);

						countimg++;
					}

					Iterator<Element> posts = doc
							.select("div.pack-list > div.pack-list-item > div.search-item-desc > a")
							.iterator();
					int countpost = 0;
					while (posts.hasNext()) {

						Element ps = posts.next();
						String post = ps.attr("href");
						pst[countpost] = "http://www.argenteam.net" + post;
						Log.v("POST", pst[countpost]);
						countpost++;
					}

					Iterator<Element> tipos = doc
							.select("div.pack-list > div.pack-list-item > div.search-item-desc > a > h1 > img")
							.iterator();
					int counttipos = 0;
					while (tipos.hasNext()) {

						Element tp = tipos.next();
						String tip = tp.attr("title");
						Log.v("TIPO", tip);
						tipo[counttipos] = tip;
						counttipos++;
					}

					break;
				} catch (IOException e) {

					e.printStackTrace();
					if (--numtries == 0)
						try {
							throw e;
						} catch (IOException e1) {
							e1.printStackTrace();
						}

				}

			}

			adapter = new LazyAdapterBuscador(SearchableActivity.this, titulo,
					imag, pst, tipo);
			response = "ok";
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.dismiss();
			lista.setAdapter(adapter);
			
		}

	}
}
