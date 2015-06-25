package ar.com.martinrevert.argenteam;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;


import java.util.HashMap;



public class MainCustomAdapter extends RecyclerView.Adapter<MainCustomAdapter.ViewHolder> {
    //private List<PostFacebook> mDataset = new ArrayList<PostFacebook>();
    private HashMap<Integer,PostFacebook> mDataset = new HashMap<Integer, PostFacebook>();
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each item in a the view
        public TextView tituloface;
        public ImageView imageface;
        public TextView fechaface;
        public TextView descripcionface;

        public ViewHolder(View v) {
            super(v);
            tituloface = (TextView) v.findViewById(R.id.tituloface);
            fechaface = (TextView)  v.findViewById(R.id.fechaface);
            imageface = (ImageView) v.findViewById(R.id.imageface);
            descripcionface = (TextView) v.findViewById(R.id.descripcionface);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainCustomAdapter(HashMap<Integer, PostFacebook> myDataset) {
        mDataset = myDataset;

        PostFacebook chusma = myDataset.get(0);
        Log.v("tituloconstructor", chusma.getTitulo() );
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainCustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemcardfacebook, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh;
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        PostFacebook getpost;
        getpost = mDataset.get(position);
        String texto = getpost.getTitulo();
        Log.v("TITULO", texto );
        String fecha = getpost.getFecha();
        Log.v("FECHA", fecha );
        String imagen = getpost.getImagen();


        //Picasso.with(holder.imageface.getContext()).load(imagen).into(holder.imageface);

        holder.tituloface.setText(texto);
        holder.fechaface.setText(fecha);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}