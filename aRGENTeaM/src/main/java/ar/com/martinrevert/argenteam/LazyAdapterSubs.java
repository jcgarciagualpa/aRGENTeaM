package ar.com.martinrevert.argenteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fedorvlasov.lazylist.ImageLoader;

public class LazyAdapterSubs extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> sub;
    private ArrayList<String> path;
    public TextView text;
    public Button btnBorrar;
    public Button btnFTP;

    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;


    public LazyAdapterSubs(Activity a, ArrayList<String> sub2,
                           ArrayList<String> ruta) {
        activity = a;
        sub = sub2;
        path = ruta;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return sub.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.itemsubs, parent, false);

        text = (TextView) vi.findViewById(R.id.titulo);
        btnFTP = (Button) vi.findViewById(R.id.btnFTP);
        btnBorrar = (Button) vi.findViewById(R.id.btnBorrar);

        text.setText(sub.get(position));
        btnFTP.setTag(path.get(position));

        btnFTP.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle param = new Bundle();

                param.putString("su", sub.get(position));
                param.putString("pa", path.get(position));
                Log.v("FTP", "FTP");
                new FTPasyncSend().execute(param);

            }

        });

        btnBorrar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View h) {

                File file = new File(path.get(position));
                file.delete();
                sub.remove(position);
                path.remove(position);
                notifyDataSetChanged();

            }

        });

        return vi;
    }

    public class FTPasyncSend extends AsyncTask<Bundle, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ToDo refresh icon en actionbar que muestre que se está laburando
        }

        @Override
        protected String doInBackground(Bundle... para) {

            SharedPreferences ftpprefs = PreferenceManager
                    .getDefaultSharedPreferences(activity);

            String ftpserver = ftpprefs.getString("ftpserver", "");
            String puerto = ftpprefs.getString("puertoftp", "");

            if (puerto.equals("")) {
                puerto = puerto + "21";
            }

            int puertoftp = Integer.parseInt(puerto);

            String directorioftp = ftpprefs.getString("directorioftp", "");
            String userftp = ftpprefs.getString("userftp", "");
            String passwordftp = ftpprefs.getString("passwordftp", "");


            Bundle param = para[0];
            String su = param.getString("su");
            String pa = param.getString("pa");
            String result;
            try {
                FTPClient ftp = new FTPClient();
                if ((puertoftp) > 0) {
                    ftp.connect(ftpserver, puertoftp);
                } else {
                    ftp.connect(ftpserver);
                }
                ftp.enterLocalPassiveMode();
                ftp.login(userftp, passwordftp);
                String estatus = ftp.getStatus();

                if (estatus != null) {
                    Log.v("LOGIN", "login OK:" + estatus);
                    ftp.changeWorkingDirectory(directorioftp);
                    //ftp.setAutodetectUTF8(true);
                    String destino = su;
                    InputStream in = new FileInputStream(pa);
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                    ftp.storeFile(destino, in);
                    in.close();
                    ftp.logout();
                    ftp.disconnect();
                    result = "'" + su + "' se ha transferido correctamente via FTP";
                } else {
                    result = "No se pudo transferir '" + su + "' via FTP. Verifique conexión y parámetros de su configuración" + estatus;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                result = "No se pudo transferir '" + su + "' via FTP. Su IP o DNS del FTP está mal configurado. Verifique tambien puerto, usuario y password";

            }

            return result;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(activity.getApplicationContext(),
                    message, duration);
            toast.show();
            activity.getBaseContext();
            Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(300);

        }

    }

}