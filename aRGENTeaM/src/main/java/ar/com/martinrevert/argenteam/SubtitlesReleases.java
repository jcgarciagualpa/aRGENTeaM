package ar.com.martinrevert.argenteam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.util.Date;
import java.util.Iterator;

public class SubtitlesReleases extends CustomMenu {

    private ListView lista;
    private LazyAdapterPeli adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subsreleases);

        Toolbar toolbar = (Toolbar) findViewById(R.id.subreltoolbar);
       // setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setTitle("Movie Releases");
        toolbar.setTitle("Movie Releases");
        toolbar.inflateMenu(R.menu.menutest);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_36dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubtitlesReleases.this.finish();
            }
        });

        lista = (ListView) findViewById(R.id.listViewMovieRel);

        if (isOnline()) {
            new AsyncRequest().execute();
        } else {
            vibrateToast(R.string.sininternet);
            finish();

        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class AsyncRequest extends AsyncTask<Void, Void, Integer> {

        ProgressDialog dialog = new ProgressDialog(SubtitlesReleases.this);

        private String pegaversiones;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //      dialog.setMessage(getResources().getString(R.string.loading));
            //      dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... arg0) {

            RSSReader reader = new RSSReader();
            String uri = "http://www.argenteam.net/rss/portal_movies_subtitles.xml";

            String[] titulo = null;
            String[] fech = null;
            String[] ver = null;
            String[] imag = null;
            String[] post = null;

            try {
                RSSFeed feed = reader.load(uri);
                final Iterator<RSSItem> items = feed.getItems().iterator();
                int size = feed.getItems().size();
                String sizestring = Integer.toString(size);
                Log.v("SIZE", sizestring);
                RSSItem item;

                titulo = new String[size];
                imag = new String[size];
                ver = new String[size];
                fech = new String[size];
                post = new String[size];
                String[] deta = new String[size];
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

            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            } finally {
                reader.close();
            }

            adapter = new LazyAdapterPeli(SubtitlesReleases.this, titulo, imag, fech, ver, post);

            return 1;

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //  dialog.dismiss();
            if (result == 1) {

                lista.setAdapter(adapter);
                lista.bringToFront();

            } else {
                vibrateToast(R.string.sinportal);
                finish();

            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
}
