package ar.com.martinrevert.argenteam;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fedorvlasov.lazylist.ImageLoader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class Tv extends CustomMenu implements OnClickListener {

	private ImageLoader imageLoader;
	private String message;
	private ImageView image;
	private ImageButton youtu;

	public String detalle;
	public String titul;
	public String post;
	public String yout;
	public String subtitulo;
	public String release;
	public String key;
	public String subtitle;
	public String ruta;
	public String sub;
	public String emule;

	Map<String, String> movie = new HashMap<String, String>();
	TreeMap<String, String> elinks = new TreeMap<String, String>();
	Map<String, String> torrents = new HashMap<String, String>();

	public String mula;
	public String btntxt;
	public String rating;
	private boolean p2p;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferencias = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		p2p = preferencias.getBoolean("p2p",false);
		image = new ImageView(Tv.this);
		image.setId(99995);
		imageLoader = new ImageLoader(getBaseContext(),"movie");

		message = getIntent().getStringExtra("passed");

		Log.v("MESSAGE", message);

		if (isOnline()) {
			new GetPage().execute(message);
		} else {
			vibrateToast("Sin Internet");
			finish();

		}
	}

	private class GetPage extends AsyncTask<String, Void, Integer> {

		ProgressDialog dialog = new ProgressDialog(Tv.this);
		private String pegaitems;
		private int palito;
		private Button btnelink;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Cargando...");
			dialog.show();
		}

		@SuppressLint("NewApi")
		@Override
		protected Integer doInBackground(String... query) {

			Document doc = null;
			int numtries = 3;
			while (true) {

				try {
					doc = Jsoup.connect(query[0]).timeout(60000)
							.cookie("tca", "Y").get();
					break;
				} catch (Exception e) {

					e.printStackTrace();
					if (--numtries == 0)
						try {
							throw e;
						} catch (Exception e1) {
							e1.printStackTrace();
                            return 0;
						}

				}
			}

			String deta = doc
					.select("div.episodes > div.episode > div.episode-info > span")
					.first().text();
			detalle = Jsoup.parse(deta).text();
			Log.v("DETAILS", detalle);

			if (doc.select(
					"div.pmovie > div.episodes > div.episode > div.episode-info > img")
					.first() != null) {
				Element poster = doc
						.select("div.pmovie > div.episodes > div.episode > div.episode-info > img")
						.first();
				post = poster.attr("src");
				Log.v("POSTER", post);
			}

			Element titulo = doc.select("div.pmovie > h1").first();
			titul = titulo.text();
			Log.v("TITULO", titul);

			Element puntaje = doc.select("div.episode-info > h1").first();
			rating = puntaje.text();

			if (doc.select("div.trailer > div.media-content > object > param")
					.first() == null) {
				if (doc.select(
						"div.trailer-single > div.media-content > object > param")
						.first() == null) {
					yout = "bla";
				} else {
					Element youtube = doc
							.select("div.trailer-single > div.media-content > object > param")
							.first();
					yout = youtube.attr("value");
				}

			} else {
				Element youtube = doc.select(
						"div.trailer > div.media-content > object > param")
						.first();
				yout = youtube.attr("value");

			}

			Log.v("YOUTUBE", yout);

			Iterator<Element> items = doc.select("div.pmovie > div.item")
					.iterator();

			StringBuilder sb = new StringBuilder();

			while (items.hasNext()) {
				Element version = items.next();
				String vers = version.text();
				sb.append(vers);
				sb.append("\n");

			}
			pegaitems = sb.toString();

			Iterator<Element> releases = doc.select(
					"div.releases > div.release").iterator();
			Iterator<Element> subt = doc.select("a[href^=/subtitles]")
					.iterator();

			while (subt.hasNext()) {

				Element subti = subt.next();
				subtitulo = subti.attr("href");
				Log.v("SUBTITULO", subtitulo);

				if (subtitulo.endsWith("CC")) {
					Log.v("CC", "NO");
				} else {
					Element rel = releases.next();
					release = rel.text();
					Log.v("RELEASES", release);
					movie.put(release, subtitulo);
				}

			}

			Elements ed2k = doc.select("a[href^=ed2k]");

			for (Element emule : ed2k) {

				mula = emule.attr("href");
				String previo = mula.substring(13);
				String theend = previo.substring(palito, previo.indexOf("|"));
				Log.v("LINKS", mula);
				Log.v("LINKS", theend);
				elinks.put(mula, theend);

			}

			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
				elinks.descendingMap();
			}

			Elements bittorents = doc.select("a[href^=magnet]");

			for (Element torrent : bittorents) {
				String terminacion = null;
				String torro = torrent.attr("href");
				
				Log.v("torro", torro);
				
				try{

				String previo = torro.substring(64);
				terminacion = previo.substring(0, previo.indexOf("&"));

                    if (terminacion.equalsIgnoreCase("acker.publichd.eu/announce")) {
                        terminacion = "Magnet Link sin nombre - Grupo PublicHD";
                    }

				}catch(Exception e){
					terminacion = "Magnet Link";
				}
				finally{
				Log.v("LINKS TORRENT", torro);
				Log.v("LINKS TORRENT", terminacion);
				torrents.put(torro, terminacion);
				}

			}

			return 1;
		}// Fin doinbackground



        @Override
		protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
			dialog.dismiss();

            if (result == 0) {
                vibrateToast("aRGENTeaM no está disponible o no tienes Internet");
                finish();
            }

			TextView titulo = new TextView(Tv.this);
			titulo.setText(titul);
			titulo.setId(99999);
			titulo.setTextColor(0xffFF992B);
			titulo.setTextSize(0, 23);

			TextView puntines = new TextView(Tv.this);
			puntines.setText(rating);
			puntines.setId(99987);
			puntines.setTextColor(0xffFFFC00);
			puntines.setTextSize(0, 20);

			TextView detall = new TextView(Tv.this);
			detall.setText(detalle);
			detall.setId(99998);
			detall.setTextColor(0xffFFFFFF);

			TextView datos = new TextView(Tv.this);
			datos.setText(pegaitems);
			datos.setId(99997);

			youtu = new ImageButton(Tv.this);
			youtu.setImageResource(R.drawable.youtube);
			youtu.setId(99975);

			TextView sinopsis = new TextView(Tv.this);
			sinopsis.setText("Sinopsis");
			sinopsis.setId(99990);
			sinopsis.setTextColor(0xffFF992B);
			sinopsis.setTextSize(0, 20);

			TextView downlsubs = new TextView(Tv.this);
			downlsubs.setText("Descargar subtitulos");
			downlsubs.setId(99989);
			downlsubs.setTextColor(0xffFF992B);
			downlsubs.setTextSize(0, 20);

			TextView downltorrents = new TextView(Tv.this);
			downltorrents.setText("Descargar torrents uTorrent");
			downltorrents.setId(99986);
			downltorrents.setTextColor(0xffFF992B);
			downltorrents.setTextSize(0, 20);

			TextView downlelinks = new TextView(Tv.this);
			downlelinks.setText("Descargar eLinks eMule");
			downlelinks.setId(99988);
			downlelinks.setTextColor(0xffFF992B);
			downlelinks.setTextSize(0, 20);

			imageLoader.DisplayImage(post, image);

			ScrollView scrollview = new ScrollView(Tv.this);
			RelativeLayout relativelayout = new RelativeLayout(Tv.this);
			relativelayout.setId(100000);

			RelativeLayout.LayoutParams paramsimage = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			paramsimage.addRule(RelativeLayout.BELOW, puntines.getId());
			paramsimage.addRule(RelativeLayout.CENTER_HORIZONTAL);
			paramsimage.width = 428;
			paramsimage.height = 280;
			image.setLayoutParams(paramsimage);

			RelativeLayout layoutsubs = new RelativeLayout(Tv.this);
			layoutsubs.setId(100001);
			RelativeLayout.LayoutParams paramssubs = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramssubs.addRule(RelativeLayout.BELOW, downlsubs.getId());
			layoutsubs.setLayoutParams(paramssubs);

			RelativeLayout layoutelinks = new RelativeLayout(Tv.this);
			layoutelinks.setId(100002);
			RelativeLayout.LayoutParams paramselinks = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramselinks.addRule(RelativeLayout.BELOW, downlelinks.getId());
			layoutelinks.setLayoutParams(paramselinks);

			RelativeLayout layoutetorrents = new RelativeLayout(Tv.this);
			layoutetorrents.setId(50000);
			RelativeLayout.LayoutParams paramstorrents = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramstorrents.addRule(RelativeLayout.BELOW, downltorrents.getId());
			layoutetorrents.setLayoutParams(paramstorrents);

			RelativeLayout.LayoutParams tituloparams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			tituloparams.addRule(RelativeLayout.ALIGN_TOP);
			titulo.setLayoutParams(tituloparams);

			RelativeLayout.LayoutParams subtiparams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			subtiparams.addRule(RelativeLayout.BELOW, detall.getId());
			downlsubs.setLayoutParams(subtiparams);

			RelativeLayout.LayoutParams elinkparams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			elinkparams.addRule(RelativeLayout.BELOW, layoutsubs.getId());
			downlelinks.setLayoutParams(elinkparams);

			RelativeLayout.LayoutParams torrentparams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			torrentparams.addRule(RelativeLayout.BELOW, layoutelinks.getId());
			downltorrents.setLayoutParams(torrentparams);

			RelativeLayout.LayoutParams sinopsisparams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			if (yout == "bla") {
				sinopsisparams.addRule(RelativeLayout.BELOW, image.getId());
			} else {
				sinopsisparams.addRule(RelativeLayout.BELOW, youtu.getId());
			}
			sinopsis.setLayoutParams(sinopsisparams);

			RelativeLayout.LayoutParams ratingparams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			ratingparams.addRule(RelativeLayout.BELOW, titulo.getId());
			ratingparams.addRule(RelativeLayout.ALIGN_LEFT);
			puntines.setLayoutParams(ratingparams);

			RelativeLayout.LayoutParams paramsdetall = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramsdetall.addRule(RelativeLayout.BELOW, sinopsis.getId());
			detall.setLayoutParams(paramsdetall);

			RelativeLayout.LayoutParams paramsdatos = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramsdatos.addRule(RelativeLayout.RIGHT_OF, image.getId());
			paramsdatos.addRule(RelativeLayout.BELOW, puntines.getId());
			datos.setLayoutParams(paramsdatos);

			RelativeLayout.LayoutParams paramsyoutu = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

			paramsyoutu.addRule(RelativeLayout.ALIGN_LEFT);
			paramsyoutu.addRule(RelativeLayout.BELOW, image.getId());
			youtu.setLayoutParams(paramsyoutu);

			relativelayout.addView(titulo);
			relativelayout.addView(image);
			relativelayout.addView(puntines);
			relativelayout.addView(detall);
			// relativelayout.addView(datos);

			if (yout != "bla") {
				relativelayout.addView(youtu);
			}
			relativelayout.addView(sinopsis);
			relativelayout.addView(downlsubs);
			relativelayout.addView(downlelinks);
			relativelayout.addView(downltorrents);

			Button btnsub = null;
			Button btntorrent = null;

			int k = 1;
			int m = 1000;
			int t = 2000;
			for (Entry<String, String> entry : movie.entrySet()) {
				key = entry.getKey();
				sub = entry.getValue();
				btnsub = new Button(Tv.this);
				btnsub.setText(key);
				btnsub.setId(k);
				btnsub.setTag(sub);
				btnsub.setOnClickListener(Tv.this);

				if (k == 1) {
					RelativeLayout.LayoutParams lay1 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lay1.addRule(RelativeLayout.ALIGN_PARENT_TOP,
							titulo.getId());

					layoutsubs.addView(btnsub, lay1);
				} else {
					RelativeLayout.LayoutParams lay2 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lay2.addRule(RelativeLayout.BELOW, btnsub.getId() - 1);
					layoutsubs.addView(btnsub, lay2);
				}
				k++;

			} // fin for movie

			for (Entry<String, String> ocurr : elinks.entrySet()) {
				String elinkbtn = ocurr.getKey();
				String txtbtn = ocurr.getValue();
				btnelink = new Button(Tv.this);
				btnelink.setId(m);
				btnelink.setTag(elinkbtn);
				btnelink.setText(txtbtn);
				btnelink.setOnClickListener(Tv.this);

				if (m == 1000) {
					RelativeLayout.LayoutParams lay3 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lay3.addRule(RelativeLayout.ALIGN_PARENT_TOP,
							RelativeLayout.TRUE);

					layoutelinks.addView(btnelink, lay3);
				} else {
					RelativeLayout.LayoutParams lay4 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lay4.addRule(RelativeLayout.BELOW, btnelink.getId() - 1);
					layoutelinks.addView(btnelink, lay4);
				}
				m++;
			} // Fin for elinks

			for (Entry<String, String> ocurrencia : torrents.entrySet()) {
				String torrentbtn = ocurrencia.getKey();
				String txttorrbtn = ocurrencia.getValue();
				// String txttorrbtn = String.valueOf(t);
				btntorrent = new Button(Tv.this);
				btntorrent.setId(t);
				btntorrent.setTag(torrentbtn);
				btntorrent.setText(txttorrbtn);
				btntorrent.setOnClickListener(Tv.this);

				if (t == 2000) {
					RelativeLayout.LayoutParams lay5 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lay5.addRule(RelativeLayout.ALIGN_PARENT_TOP,
							RelativeLayout.TRUE);

					layoutetorrents.addView(btntorrent, lay5);
				} else {
					RelativeLayout.LayoutParams lay6 = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lay6.addRule(RelativeLayout.BELOW, btntorrent.getId() - 1);
					layoutetorrents.addView(btntorrent, lay6);
				}
				t++;
			} // Fin for torrents

			relativelayout.addView(layoutsubs);
			relativelayout.addView(layoutelinks);
			relativelayout.addView(layoutetorrents);
			scrollview.addView(relativelayout);
			setContentView(scrollview);

			youtu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Log.v("YOUTUBE", "Click!");
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(yout)));
				}
			});

		}// Fin onPostExecute
	}// Fin asyctask

	@Override
	public void onClick(View v) {

	
		if (v.getId() >= 1 && v.getId() < 1000) {

			// String indice = String.valueOf(v.getId());
			String tag = (String) v.getTag();
			String URIpost = "http://www.argenteam.net" + tag;
			Intent subintent = new Intent(Tv.this, DownloadFile.class);
			subintent.putExtra("passed", URIpost);
			Tv.this.startActivityForResult(subintent, 0);

		} else {
			if (v.getId() >= 2000 && v.getId() < 3000 && p2p == false) {
				Log.v("TORRENT", "BOTON");
				String tag = (String) v.getTag();

				String URIpost = tag;
				Intent subintent = new Intent(Tv.this,
						BittorrentRequest.class);
				subintent.putExtra("passed", URIpost);
				Tv.this.startActivityForResult(subintent, 0);
			} else {
				if (v.getId() >= 1000 && v.getId() < 2000 && p2p == false) {
				Log.v("ELINK", "BOTON!");
				String tag = (String) v.getTag();
				String URIpost = tag;
				Intent subintent = new Intent(Tv.this, EmuleRequest.class);
				subintent.putExtra("passed", URIpost);
				Tv.this.startActivityForResult(subintent, 0);
				
				}else {
					String tag = (String) v.getTag();
					if (isP2P(tag) == true) {
						Intent sharingIntent = new Intent(
								android.content.Intent.ACTION_VIEW);
						sharingIntent
								.addCategory(android.content.Intent.CATEGORY_BROWSABLE);
						sharingIntent.setData(Uri.parse(tag));
						startActivity(sharingIntent);
					} else {

						vibrateToast("No hay apps instaladas para manejar el requerimiento. Deschequear parametro 'P2P off' en Configuraciones o instalar una app para manejar links P2P");
					}
					
				}
			}
		}

	}
	
}
	