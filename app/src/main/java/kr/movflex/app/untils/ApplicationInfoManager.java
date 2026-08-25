package kr.movflex.app.untils;

import android.content.Context;
import android.os.Build;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.movflex.app.webview.CustomWebView;


public class ApplicationInfoManager {
    private static volatile ApplicationInfoManager instance = null;
    private Context context;
    private List<String> deepLinkUrls;
    private List<View> windowViews = new ArrayList<>();
    private List<CustomWebView> windowWebViews = new ArrayList<>();

    private List<String> routeUrls = new ArrayList<>();


    public static ApplicationInfoManager getInstance() {
        if (null == instance) {
            synchronized (ApplicationInfoManager.class) {
                instance = new ApplicationInfoManager();
            }
        }
        return instance;
    }

    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<String> getDeepLinkUrls() {
        return deepLinkUrls;
    }

    public void setDeepLinkUrls(List<String> deepLinkUrls) {
        this.deepLinkUrls = deepLinkUrls;
    }

    public void addDeepLinkUrl(String url) {
        if (deepLinkUrls == null)
            deepLinkUrls = new ArrayList<>();

        deepLinkUrls.add(url);
    }

    public List<View> getWindowViews() {
        return windowViews;
    }

    public void setWindowViews(List<View> windowViews) {
        this.windowViews = windowViews;
    }

    public void addWindowViews(View view) {
        this.windowViews.add(view);
    }

    public void deleteWindowViewsLastIndex() {
        if (windowViews.size() > 0) {
            this.windowViews.remove(windowViews.size() - 1);
        }
    }

    public List<CustomWebView> getWindowWebViews() {
        return windowWebViews;
    }

    public void setWindowWebViews(List<CustomWebView> windowWebViews) {
        this.windowWebViews = windowWebViews;
    }

    public void addWindowWebViews(CustomWebView view) {
        this.windowWebViews.add(view);
    }

    public void deleteWindowWebViewsLastIndex() {
        if (windowWebViews.size() > 0) {
            this.windowWebViews.remove(windowWebViews.size() - 1);
        }
    }


    public List<String> getRouteUrls() {
        return routeUrls;
    }

    public void setRouteUrls(List<String> routeUrls) {
        this.routeUrls = routeUrls;
    }

    public void addRouteUrls(String url) {
        if (routeUrls == null)
            routeUrls = new ArrayList<>();

        routeUrls.add(url);
    }
}
