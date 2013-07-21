package ar.com.martinrevert.argenteam;

import java.io.File;

import java.util.ArrayList;

import android.annotation.SuppressLint;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

public class FTPActivity extends CustomMenu {

	String dirPath = "/aRGENTeaM//mis_subtitulos//";

	private ListView lista;
	LazyAdapterSubs adapter;

	public ArrayList<String> ruta = new ArrayList<String>();
	public ArrayList<String> sub = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.missubtitulos);
		lista = (ListView) findViewById(R.id.listSubs);

		new GetSubsSDcard().execute();

	}

	public class GetSubsSDcard extends AsyncTask<Void, Void, Void> {

		File[] files;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@SuppressLint("NewApi")
		@Override
		protected Void doInBackground(Void... arg0) {

			int sdkVersion = Build.VERSION.SDK_INT;

			if (sdkVersion == 7) {

				String archivos = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + dirPath;
				files = new File(archivos).listFiles();
			} else {
				files = Environment.getExternalStoragePublicDirectory(dirPath)
						.listFiles();
			}

			if (files != null) {

				for (File file : files) {
					String name = file.getName();
					String path = file.getAbsolutePath();
					Log.v("FILE", name);
					if (name.endsWith("srt")) {
						sub.add(name);
						ruta.add(path);
					}
				}

				adapter = new LazyAdapterSubs(FTPActivity.this, sub, ruta);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			lista.setAdapter(adapter);
		}

	}

}
