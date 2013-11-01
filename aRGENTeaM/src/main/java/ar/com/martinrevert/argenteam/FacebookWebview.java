package ar.com.martinrevert.argenteam;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class FacebookWebview extends CustomMenu {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WebView webview;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook);
        webview = (WebView) findViewById(R.id.webview);

        String url = getIntent().getStringExtra("passed");
        Log.v("URL webview ", url);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.loadUrl(url);

    }


}
