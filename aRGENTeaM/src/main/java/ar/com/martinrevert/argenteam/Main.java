package ar.com.martinrevert.argenteam;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Main extends BaseActivity {
    private DrawerLayout mDrawerLayout;
    private ListView lista;
    private String[] options;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
     //   toolbar.inflateMenu(R.menu.menu);
        //


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
            //ToDo ver qué hacemos en este caso
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