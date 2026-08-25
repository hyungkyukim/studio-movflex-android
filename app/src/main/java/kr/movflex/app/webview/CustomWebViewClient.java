package kr.movflex.app.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

import kr.movflex.app.activity.MainActivity;
import kr.movflex.app.listener.OnPageLoadedListener;

public class CustomWebViewClient extends WebViewClient {
    private MainActivity activity;
    private OnPageLoadedListener listener;


    public CustomWebViewClient(MainActivity activity) {
        this.activity = activity;
    }
    public CustomWebViewClient(Activity activity, OnPageLoadedListener listener) {
        this.activity = (MainActivity) activity;
        this.listener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) { // return 값으로 링크를 어떻게 처리할 까를 결정
        //intent 호출 시 net::ERR_UNKNOWN_URL_SCHEME 에러
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
            Intent intent = null;
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                Uri uri = Uri.parse(intent.getDataString());
                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true; //true = 웹뷰를 포함하고 있는 앱이 처리, WebView는 해당 URL을 렌더하지 않는다.
            } catch (URISyntaxException ex) {
                return false; //false = 지금 WebView가 처리
            }
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (listener != null) {
            listener.onFinish();
        }
        CookieManager.getInstance().flush();
        super.onPageFinished(view, url);
    }

}
