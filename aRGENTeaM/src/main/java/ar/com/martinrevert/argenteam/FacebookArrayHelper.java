package ar.com.martinrevert.argenteam;

import android.net.Uri;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by martin on 02/11/14.
 */
/*
public class FacebookArrayHelper {

    private String[] titulo = null;
    private String[] fech = null;
    private String[] ver = null;
    private String[] imag = null;
    private String[] post = null;

    public ArrayList getArrayList() {
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
            return arraylista;

        } finally {
            reader.close();
        }
    }
}
*/