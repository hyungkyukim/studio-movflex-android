package kr.movflex.app.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CustomWebView extends WebView {
    public CustomWebView(Context context) {
        super(context);
        init();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        WebSettings set = getSettings();
        set.setJavaScriptEnabled(true);
        set.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        set.setAllowFileAccess(true);
        set.setDomStorageEnabled(true);
        set.setLoadWithOverviewMode(true);
        set.setDefaultTextEncodingName("UTF-8");
        set.setJavaScriptCanOpenWindowsAutomatically(true);
        set.setPluginState(WebSettings.PluginState.ON);
        set.setSupportMultipleWindows(true);
        set.setUseWideViewPort(true);
        set.setCacheMode(WebSettings.LOAD_NO_CACHE);
        set.setSupportZoom(true);
        set.setBuiltInZoomControls(true);
        set.setDisplayZoomControls(false);
    }
}
