package ar.com.martinrevert.argenteam;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Main extends CustomMenu {
    ListView lista;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ToDo armar borrado inteligente de caché y ver donde se guarda
        //FileCache fileCache = new FileCache(getBaseContext());
        //fileCache.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources();

        lista = (ListView) findViewById(R.id.listamenu);
        String[] options = res.getStringArray(R.array.menu_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, options);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                switch (position) {
                    case 0:
                        startActivity(new Intent(Main.this, SubtitlesReleases.class));
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

        });

    }


}