package ar.com.martinrevert.argenteam;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.fedorvlasov.lazylist.ImageLoader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class TvSeasons extends CustomMenu implements OnClickListener {

    private ImageLoader imageLoader;
    private ImageView image;
    private Toolbar toolbar;
    private LinearLayout container;
    private String detalle;
    private String titul;
    private String post;
    private String key;
    private String subtitle;
    private String sub;
    private String rating;
    private String season;
    private String tag;

    TreeMap<String, String> temporadas = new TreeMap<String, String>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        container = new LinearLayout(TvSeasons.this);
        container.setOrientation(LinearLayout.VERTICAL);

        toolbar = new Toolbar(TvSeasons.this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("TV Seasons");
        toolbar.setId(100003);
        toolbar.setBackgroundColor(0xffFF0000);

        image = new ImageView(TvSeasons.this);
        image.setId(99995);
        imageLoader = new ImageLoader(getBaseContext(), "movie");

        String message = getIntent().getStringExtra("passed");

        if (isOnline()) {
            new GetPage().execute(message);
        } else {
            vibrateToast(R.string.sininternet);
            finish();

        }


    }


    private class GetPage extends AsyncTask<String, Void, Integer> {

        ProgressDialog dialog = new ProgressDialog(TvSeasons.this);
        private String pegaitems;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getResources().getString(R.string.loading));
            dialog.show();
        }


        @Override
        protected Integer doInBackground(String... query) {

            Document doc = null;
            int numtries = 3;
            while (true) {

                try {
                    doc = Jsoup.connect(query[0]).timeout(10000)
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

            String deta = doc.select("div.details").first().text();
            detalle = Jsoup.parse(deta).text();
            Log.v("DETAILS", detalle);

            if (doc.select("div.pmovie > img").first() == null) {
                post = "http://www.argenteam.net/images/header-background.gif";
            } else {
                Element poster = doc.select("div.pmovie > img").first();
                post = poster.attr("src");
            }
            Log.v("POSTER", post);


            Element titulo = doc.select("div.pmovie > h1").first();
            titul = titulo.text();
            Log.v("TITULO", titul);

            if (doc.select("div.score").first() != null) {
                Element puntaje = doc.select("div.score").first();
                rating = puntaje.text();
                rating = rating.substring(0, 1) + "."
                        + rating.substring(1, rating.length());
            } else {
                rating = "Sin puntaje";
            }
            Log.v("RATING", rating);


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

            Iterator<Element> seasons = doc.select("div.season-list > div.season-item > a").iterator();


            while (seasons.hasNext()) {
                Element sea = seasons.next();
                season = sea.text();
                tag = sea.attr("href");
                Log.v("TEMPORADA", season + " " + tag);
                temporadas.put(tag, season);

            }

            return 1;
        }// Fin doinbackground

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result == 0) {
                vibrateToast(R.string.sinportal);
                finish();
            }

            TextView titulo = new TextView(TvSeasons.this);
            titulo.setText(titul);
            titulo.setId(99999);
            titulo.setTextColor(0xffFF992B);
            titulo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

            RatingBar rate = new RatingBar(TvSeasons.this, null, android.R.attr.ratingBarStyleSmall);
            if (rating.equals("Sin puntaje")) {
                rate.setRating(0.0f);
            } else {
                rate.setRating(Float.parseFloat(rating) / 2);
            }
            rate.setNumStars(10);
            rate.setMax(10);
            rate.setId(99987);
            rate.setStepSize((float) 0.01);
            rate.setIsIndicator(true);


            TextView detall = new TextView(TvSeasons.this);
            detall.setText(detalle);
            detall.setId(99998);
            detall.setTextColor(0xffFFFFFF);

            TextView datos = new TextView(TvSeasons.this);
            datos.setText(pegaitems);
            datos.setId(99997);


            TextView sinopsis = new TextView(TvSeasons.this);
            sinopsis.setText(R.string.plot);
            sinopsis.setId(99990);
            sinopsis.setTextColor(0xffFF992B);
            sinopsis.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            TextView downlsubs = new TextView(TvSeasons.this);
            downlsubs.setText(R.string.seasons);
            downlsubs.setId(99989);
            downlsubs.setTextColor(0xffFF992B);
            downlsubs.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


            imageLoader.DisplayImage(post, image);

            ScrollView scrollview = new ScrollView(TvSeasons.this);
            RelativeLayout relativelayout = new RelativeLayout(TvSeasons.this);
            relativelayout.setId(100000);

            RelativeLayout.LayoutParams paramsimage = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            paramsimage.addRule(RelativeLayout.BELOW, rate.getId());
            paramsimage.addRule(RelativeLayout.ALIGN_LEFT);

            paramsimage.width = 320;
            paramsimage.height = 472;
            image.setLayoutParams(paramsimage);

            RelativeLayout layouttemporadas = new RelativeLayout(TvSeasons.this);
            layouttemporadas.setId(100001);
            RelativeLayout.LayoutParams paramssubs = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            paramssubs.addRule(RelativeLayout.BELOW, downlsubs.getId());
            layouttemporadas.setLayoutParams(paramssubs);

            RelativeLayout.LayoutParams tituloparams = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            tituloparams.addRule(RelativeLayout.ALIGN_TOP);
            titulo.setLayoutParams(tituloparams);

            RelativeLayout.LayoutParams subtiparams = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            subtiparams.addRule(RelativeLayout.BELOW, detall.getId());
            downlsubs.setLayoutParams(subtiparams);

            RelativeLayout.LayoutParams sinopsisparams = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            sinopsisparams.addRule(RelativeLayout.BELOW, image.getId());
            sinopsis.setLayoutParams(sinopsisparams);

            RelativeLayout.LayoutParams ratingparams = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ratingparams.addRule(RelativeLayout.BELOW, titulo.getId());
            ratingparams.addRule(RelativeLayout.ALIGN_LEFT);
            rate.setLayoutParams(ratingparams);

            RelativeLayout.LayoutParams paramsdetall = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            paramsdetall.addRule(RelativeLayout.BELOW, sinopsis.getId());
            detall.setLayoutParams(paramsdetall);

            RelativeLayout.LayoutParams paramsdatos = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            paramsdatos.addRule(RelativeLayout.RIGHT_OF, image.getId());
            paramsdatos.addRule(RelativeLayout.BELOW, rate.getId());
            datos.setLayoutParams(paramsdatos);

            relativelayout.addView(titulo);
            relativelayout.addView(image);
            relativelayout.addView(rate);
            relativelayout.addView(detall);
            relativelayout.addView(datos);


            relativelayout.addView(sinopsis);
            relativelayout.addView(downlsubs);


            Button btnsub;


            int k = 1;

            for (Entry<String, String> entry : temporadas.entrySet()) {
                sub = entry.getKey();
                key = entry.getValue();
                btnsub = new Button(TvSeasons.this);
                btnsub.setText(key);
                btnsub.setId(k);
                btnsub.setTag(sub);
                btnsub.setOnClickListener(TvSeasons.this);

                if (k == 1) {
                    RelativeLayout.LayoutParams lay1 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lay1.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                            titulo.getId());

                    layouttemporadas.addView(btnsub, lay1);
                } else {
                    RelativeLayout.LayoutParams lay2 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lay2.addRule(RelativeLayout.BELOW, btnsub.getId() - 1);
                    layouttemporadas.addView(btnsub, lay2);
                }
                k++;

            } // fin for temporadas

            container.addView(toolbar);
            relativelayout.addView(layouttemporadas);
            scrollview.addView(relativelayout);
            container.addView(scrollview);

            setContentView(container);

        }// Fin onPostExecute
    }// Fin asyctask

    @Override
    public void onClick(View v) {

        if (v.getId() >= 1 && v.getId() < 1000) {

            // String indice = String.valueOf(v.getId());
            String link = (String) v.getTag();
            String URIpost = "http://www.argenteam.net" + link;
            Intent subintent = new Intent(TvSeasons.this, TvEpisodes.class);
            subintent.putExtra("passed", URIpost);
            TvSeasons.this.startActivityForResult(subintent, 0);


        }

    }
}