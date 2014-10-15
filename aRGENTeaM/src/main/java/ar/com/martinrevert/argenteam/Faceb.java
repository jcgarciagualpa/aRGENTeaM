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
import android.widget.ListView;


public class Faceb extends CustomMenu {

    ListView lista;
    LazyAdapterFacebook adapter;

    String[] titulo = null;
    String[] fech = null;
    String[] ver = null;
    String[] imag = null;
    String[] post = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista);
        lista = (ListView) findViewById(R.id.listView1);
        if (isOnline()) {
            new AsyncRequest().execute();
        } else {
            vibrateToast(R.string.sininternet);
            finish();

        }

    }

    private class AsyncRequest extends AsyncTask<Void, Void, Integer> {

        ProgressDialog dialog = new ProgressDialog(Faceb.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getResources().getString(R.string.loading));
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... arg0) {

            RSSReader reader = new RSSReader();
            String uri = "https://www.facebook.com/feeds/page.php?format=rss20&id=97037155997";
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
                int count = 0;

                while (items.hasNext()) {

                    item = items.next();

                    String previo = item.getTitle();
                    titulo[count] = Jsoup.parse(previo).text();

                    Log.v("TITLE", titulo[count]);
                    String content = item.getDescription();
                    Document doc = Jsoup.parse(content);

                    String link;
                    if (doc.select("img").first() == null) {
                        link = "http://www.argenteam.net/images/header-background.gif";
                    } else {
                        Element imagen = doc.select("img").first();
                        link = imagen.attr("src");
                    }

                    imag[count] = link;
                    Log.v("IMAGEN", imag[count]);

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

            adapter = new LazyAdapterFacebook(Faceb.this, titulo, imag,
                    fech, ver, post);

            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result == 1) {
                lista.setAdapter(adapter);
            } else {
                vibrateToast(R.string.sinfacebook);
                finish();

            }



        }

    }

}
