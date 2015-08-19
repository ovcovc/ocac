package com.piotr.app.insurance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by Piotr on 2015-08-18.
 */
public class Web1Fragment extends Fragment {

    WebView webView;

    public Web1Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.web_layout, container, false);

        webView = (WebView)rootView.findViewById(R.id.webView);

        WebSettings wbset = webView.getSettings();
        wbset.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        String url="http://amabee.pl/apka/bazawiedzy.html";

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        return rootView;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}