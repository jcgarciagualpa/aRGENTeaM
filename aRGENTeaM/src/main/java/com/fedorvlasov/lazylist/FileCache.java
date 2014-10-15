package com.fedorvlasov.lazylist;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileCache {


    private File cacheDir;

    public FileCache(Context context) {
        //Find the dir to save cached images
        //   if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        //       cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"data/data/ar.com.martinrevert.argenteam/cache");
        //   else
        //
        //Limpiar luego de que una buena parte de los usuarios tenga una versión mayor a 2.2.157
        File dir1 = new File(Environment.getExternalStorageDirectory() + "/aRGENTeaM/cache");
        if (dir1.exists()) {
            dir1.delete();
        }
        //ToDo Buscar forma de borrar vieja instalación sharedpreferences
        String dirPath = context.getFilesDir().getPath()+"/"+"shared_prefs/SplashActivity.xml";
        File root = new File(dirPath);
        root.setWritable(true,false);
        Log.v("can write?", String.valueOf(root.canWrite()));
        if (root.exists()) {
            root.delete();
            }



        cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename = String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}