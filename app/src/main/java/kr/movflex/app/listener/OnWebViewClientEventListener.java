package kr.movflex.app.listener;

import android.webkit.WebView;

public interface OnWebViewClientEventListener {
    void onWebViewClient(WebView when, String url);
}
