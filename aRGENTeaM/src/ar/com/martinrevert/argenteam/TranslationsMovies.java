package ar.com.martinrevert.argenteam;

import java.util.Date;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.app.ProgressDialog;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.lang.StringBuilder;

import android.widget.ListView;

public class TranslationsMovies extends CustomMenu {

	ListView lista;
	LazyAdapter adapter;

	String[] titulo = null;
	String[] fech = null;
	String[] ver = null;
	String[] imag = null;
	String[] post = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista);
		lista = (ListView) findViewById(R.id.listView1);
		if (isOnline() == true) {
			new AsyncRequest().execute();
		} else {
			vibrateToast("Sin Internet");
			finish();

		}
	}

	private class AsyncRequest extends AsyncTask<Void, Void, Void> {

		ProgressDialog dialog = new ProgressDialog(TranslationsMovies.this);

		private String pegaversiones;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.setMessage("Cargando...");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			RSSReader reader = new RSSReader();
			String uri = "http://www.argenteam.net/rss/portal_movies_translations.xml";
			try {
				RSSFeed feed = reader.load(uri);
				final Iterator<RSSItem> items = feed.getItems().iterator();
				int size = feed.getItems().size();
				String sizestring = Integer.toString(size);
				Log.v("SIZE", sizestring);
				RSSItem item = null;

				titulo = new String[size];
				imag = new String[size];
				ver = new String[size];
				fech = new String[size];
				post = new String[size];
				int count = 0;

				while (items.hasNext()) {

					item = items.next();

					titulo[count] = item.getTitle();
					Log.v("TITLE", titulo[count]);
					String content = item.getContent();
					Document doc = Jsoup.parse(content);
					Element imagen = doc.select("img").first();
					String link = imagen.attr("src");
					imag[count] = link;
					Log.v("IMAGEN", imag[count]);

					Iterator<Element> versiones = doc.select("strong")
							.iterator();

					StringBuilder sb = new StringBuilder();

					while (versiones.hasNext()) {
						Element version = versiones.next();
						String vers = version.text();
						sb.append(vers);
						sb.append("\n");
						Log.v("VERSION", vers);
					}
					pegaversiones = sb.toString();
					Log.v("pegaversiones", pegaversiones);
					ver[count] = pegaversiones;

					Uri url = item.getLink();
					post[count] = url.toString();
					Log.v("POST", post[count]);

					Date fecha = item.getPubDate();
					String fechaza = fecha.toLocaleString();
					fech[count] = fechaza;
					Log.v("DATE", fech[count]);
					count++;
				}

			} catch (RSSReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} finally{
				reader.close();
			}
			adapter = new LazyAdapter(TranslationsMovies.this, titulo, imag,
					fech, ver, post);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.dismiss();
			lista.setAdapter(adapter);

		}

	}

}
