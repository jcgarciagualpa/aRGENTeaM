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

public class LazyAdapterFacebook extends BaseAdapter {
    
	int position;
    private Activity activity;
    private String[] titulo;
    private String[] imagen;
    private String[] fecha;
    private String[] version;
    private String[] post;
    
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
	private View vi; 
    
   
    
    public LazyAdapterFacebook(Activity a, String[] t, String[] i, String[] f, String[] v, String[] p) {
        activity = a;
        titulo=t;
        imagen=i;
        fecha=f;
        version=v;
        post=p;
       
       
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
        vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.item, null);
     TextView fech=(TextView)vi.findViewById(R.id.fech);
     TextView text=(TextView)vi.findViewById(R.id.titulo);
     TextView vers=(TextView)vi.findViewById(R.id.version);
     ImageView image=(ImageView)vi.findViewById(R.id.image);
     
     text.setText(titulo[position]);
     fech.setText("Publicado: "+fecha[position]);
     vers.setText(version[position]);
     
     imageLoader.DisplayImage(imagen[position], image);
     
     
     
     vi.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			
			
			
				String URIpost = post[position].toString();
				Log.v("URL", URIpost);
				Intent FBPage = new Intent(activity,FacebookWebview.class);
				FBPage.putExtra("passed", URIpost);
				activity.startActivityForResult(FBPage, 0);
				

		}

		
    	 
     });
     
        return vi;
    }


}