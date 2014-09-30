package ar.com.martinrevert.argenteam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;


public class DownloadFile extends CustomMenu {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String message;
        message = getIntent().getStringExtra("passed");
        Log.v("URL", message);

        if (isOnline()) {
            new DownloadFileAsync().execute(message);
        } else {
            vibrateToast(R.string.sininternet);
            finish();

        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Descargando y descomprimiendo subtítulo en tu SD Card");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    public String getPath() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("path", "");
    }

    public class DownloadFileAsync extends AsyncTask<String, String, String> {
        public File rootDir = new File(getPath());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.v("ROOTDIR", rootDir.toString());
            if (rootDir.toString().equals("")) {
                vibrateToast(R.string.nodirsubs);
                finish();
            }

            //Hay que actualizar esto para usar DialogFragments
            //http://android-developers.blogspot.in/2012/05/using-dialogfragments.html
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(rootDir + "/temp.zip");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                Decompress d = new Decompress(rootDir.toString() + "/temp.zip", rootDir.toString());
                d.unzip();
                File file = new File(rootDir + "/temp.zip");
                file.delete();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            mProgressDialog.dismiss();
            finish();
        }
    }

    public class Decompress {
        private String _zipFile;
        private String _location;

        public Decompress(String zipFile, String location) {
            _zipFile = zipFile;
            _location = location;

            _dirChecker("");
        }

        public void unzip() {
            try {
                FileInputStream fin = new FileInputStream(_zipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    Log.v("Decompress", "Unzipping " + ze.getName());

                    if (ze.isDirectory()) {
                        _dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(_location + "/" + ze.getName());
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }

                        zin.closeEntry();
                        fout.close();


                    }

                }
                zin.close();
            } catch (Exception e) {
                Log.e("Decompress", "Exception", e);
            }

        }

        private void _dirChecker(String dir) {
            File f = new File(_location + dir);

            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }
    }

}