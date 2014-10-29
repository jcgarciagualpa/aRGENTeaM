package ar.com.martinrevert.argenteam;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.fedorvlasov.lazylist.ImageLoader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class TvSeasons extends CustomMenu implements OnClickListener {

    private ImageLoader imageLoader;
    private ImageView image;


    public String detalle;
    public String titul;
    public String post;
    public String key;
    public String subtitle;
    public String sub;


    TreeMap<String, String> temporadas = new TreeMap<String, String>();


    public String rating;
    public String season;
    public String tag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menutest, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ToDo implementar favoritos toggle corazón en actionbar/toolbar
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();

                return true;
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


            relativelayout.addView(layouttemporadas);

            scrollview.addView(relativelayout);
            setContentView(scrollview);

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