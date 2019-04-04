package com.oc.liza.go4lunch.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.oc.liza.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

public class WebviewActivity extends AppCompatActivity {
    @Nullable
    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        configWebview();
        setContentView(webView);
    }

    private void configWebview() {

        //Create new webview to show the article and set the app as webclient
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());

        //Get the url saved in shared preferences
        SharedPreferences sharedPref = this.getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE);
        String url = sharedPref.getString("Website", null);
        webView.loadUrl(url);
    }
}
