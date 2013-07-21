package ar.com.martinrevert.argenteam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

import ar.com.martinrevert.argenteam.Peli;
import ar.com.martinrevert.argenteam.R;
import ar.com.martinrevert.argenteam.TvSeasons;


public class LazyAdapterBuscador extends BaseAdapter {
    
	int position;
    private Activity activity;
    private String[] titulo;
    private String[] imagen;
    private String[] post;
    private String[] tipo;
    
    
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    
   
    
    public LazyAdapterBuscador(Activity a, String[] t, String[] i, String[] p, String[] tp) {
        activity = a;
        titulo=t;
        imagen=i;
        post=p;
        tipo=tp;
       
       
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext(),"movie");
    }

    public int getCount() {
        return titulo.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.itembusq, null);
     
     TextView text=(TextView)vi.findViewById(R.id.titulo);
     
     TextView tip=(TextView)vi.findViewById(R.id.tipo);
    
     ImageView image=(ImageView)vi.findViewById(R.id.image);
     
     text.setText(titulo[position]);
     tip.setText(tipo[position]);
     
     
     imageLoader.DisplayImage(imagen[position], image);
     
     vi.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			
			
						
			if(tipo[position].equals("Pelicula")) {
				String URIpost = post[position].toString();
				Log.v("URL", URIpost);
				Intent peliPage = new Intent(activity,Peli.class);
				peliPage.putExtra("passed", URIpost);
				activity.startActivityForResult(peliPage, 0);
				
				}
				else  {
					String URIpost = post[position].toString();
					Log.v("URL", URIpost);
					Intent peliPage = new Intent(activity,TvSeasons.class);
					peliPage.putExtra("passed", URIpost);
					activity.startActivityForResult(peliPage, 0);
				}

		}

		
    	 
     });
     
        return vi;
    }


}