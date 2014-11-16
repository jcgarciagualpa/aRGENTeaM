package ar.com.martinrevert.argenteam;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main extends BaseActivity {
    private DrawerLayout mDrawerLayout;
    private ListView lista;
    private String[] options;
    private ActionBarDrawerToggle toggle;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //private List<PostFacebook> bababuba = new ArrayList<PostFacebook>();
    private HashMap<Integer,PostFacebook> bababuba = new HashMap<Integer, PostFacebook>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);





         //bababuba.put(0, new PostFacebook("titulo","blabla", "blulbu", "nanana"));
         //bababuba.put(1, new PostFacebook("otrotit","bleble", "blulbu", "nonono"));
         //bababuba.put(2,new PostFacebook());



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lista = (ListView) findViewById(R.id.listamenu);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        Resources res = getResources();
        options = res.getStringArray(R.array.menu_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, options);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon

        toggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,     /* DrawerLayout object */
                toolbar,
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                 getSupportActionBar().setTitle(R.string.app_name);
                 invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                 getSupportActionBar().setTitle("Elige tu opción");
                 invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(toggle);

        if (savedInstanceState == null) {
            //ToDo ver qué hacemos en este caso ¿Rotación?¿Destrucción activity?
        }
        //recycler view***************************************************
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a Grid layout manager
        mLayoutManager = new GridLayoutManager(Main.this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)

        if (isOnline()) {
            new getPostFacebook().execute();
        } else {
            vibrateToast(R.string.sininternet);
            finish();
        }



    }

    private class getPostFacebook extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            RSSReader reader = new RSSReader();
            String uri = "https://www.facebook.com/feeds/page.php?format=rss20&id=97037155997";
            try {
                RSSFeed feed = reader.load(uri);
                final Iterator<RSSItem> items = feed.getItems().iterator();
                int size = feed.getItems().size();
                String sizestring = Integer.toString(size);
                Log.v("SIZE", sizestring);
                RSSItem item;
                int count = 0;
                while (items.hasNext()) {

                    item = items.next();
                    PostFacebook post = new PostFacebook();

                    String previo = item.getTitle();
                    post.setTitulo(Jsoup.parse(previo).text());

                    String content = item.getDescription();
                    Document doc = Jsoup.parse(content);
                    String link;
                    if (doc.select("img").first() == null) {
                        link = "http://www.argenteam.net/images/header-background.gif";
                    } else {
                        Element imagen = doc.select("img").first();
                        link = imagen.attr("src");
                    }

                    post.setImagen(link);

                    Uri url = item.getLink();
                    post.setLinkpost(url.toString());

                    Date fecha = item.getPubDate();
                    String fechaza = fecha.toLocaleString();
                    post.setFecha(fechaza);
                    bababuba.put(count, post);
                    count++;
                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                reader.close();
            Integer largo = bababuba.size();
            Log.v("bababuba", largo.toString());

                for (Map.Entry<Integer, PostFacebook> entry : bababuba.entrySet()) {
                    Integer key = entry.getKey();
                    PostFacebook sub = entry.getValue();
                    Log.v("DATA", sub.getLinkpost());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            mAdapter = new MainCustomAdapter(bababuba);
            mRecyclerView.setAdapter(mAdapter);
            //recycler view***************************************************
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ToDo armar borrado inteligente de caché y ver donde se guarda
        //FileCache fileCache = new FileCache(getBaseContext());
        //fileCache.clear();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    startActivity(new Intent(Main.this, SubtitlesReleases.class));
                  //  setTitle(options[position]);
                  //  mDrawerLayout.closeDrawer(lista);
                    break;
                case 1:
                    startActivity(new Intent(Main.this,
                            SubtitlesReleasesTV.class));
                    break;
                case 2:
                    startActivity(new Intent(Main.this,
                            TranslationsMovies.class));
                    break;
                case 3:
                    startActivity(new Intent(Main.this, TranslationsTV.class));
                    break;
                case 4:
                    startActivity(new Intent(Main.this, FTPActivity.class));
                    break;
                case 5:
                    startActivity(new Intent(Main.this, Faceb.class));
                    break;
                default:
                    break;
            }
        }
    }

}