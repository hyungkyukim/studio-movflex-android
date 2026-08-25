package kr.movflex.app.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.core.view.WindowCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.onesignal.OneSignal;

import kr.movflex.app.BR;
import kr.movflex.app.R;

import kr.movflex.app.common.Constants;
import kr.movflex.app.databinding.ActivityMainBinding;
import kr.movflex.app.listener.OnPageLoadedListener;
import kr.movflex.app.untils.ApplicationInfoManager;
import kr.movflex.app.vms.activity.MainActivityVM;
import kr.movflex.app.webview.CustomChromeClient;
import kr.movflex.app.webview.CustomWebView;
import kr.movflex.app.webview.CustomWebViewClient;
import kr.movflex.app.webview.S3AppNative;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity : ";
    private ActivityMainBinding binding;
    private MainActivityVM vm;
    private CustomWebView webView;
    private long backPressedTime = 0;

    private GeolocationPermissions.Callback callback;
    private String origin;

    private boolean isReady = false;
    private String targetUrl;

    public static Intent buildIntent(Context context, Boolean permission) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("permission", permission);
        return i;
    }

    public static Intent buildIntent(Context context, String target) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("target", target);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor("#FAFAFA");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        vm = new MainActivityVM(this, savedInstanceState);
        binding.setVariable(BR.vm, vm);
        binding.executePendingBindings();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        boolean permission = getIntent().getBooleanExtra("permission", false);
        if (permission) permissionCheck();

        String target = getIntent().getStringExtra("target");
        if (target != null && !target.equals("")) {
            targetUrl = Constants.BASE_URL + getIntent().getStringExtra("target");
            if (isReady) {
                webView.loadUrl(targetUrl);
                targetUrl = "";
            } else
                ApplicationInfoManager.getInstance().addRouteUrls(targetUrl);
        }

        webView = binding.webView;
        setWebView();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String target = intent.getStringExtra("target");
        if (isReady && target != null && !target.equals(""))
            binding.webView.loadUrl(Constants.BASE_URL + target);
        else
            ApplicationInfoManager.getInstance().addRouteUrls(Constants.BASE_URL + target);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CookieManager.getInstance().flush();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieManager.getInstance().flush();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public void openWindowWebView(Message resultMsg) {
        final RelativeLayout mEmailLinear = binding.windowRoot;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View emailNewField = inflater.inflate(R.layout.window_webview_layout, null);
        CustomWebView windowWebView = new CustomWebView(this);

        WebSettings webSettings = windowWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setTextZoom(100);
        windowWebView.setWebViewClient(new CustomWebViewClient(this));
        windowWebView.setWebChromeClient(new CustomChromeClient(this));
        windowWebView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        windowWebView.getSettings().setDatabaseEnabled(true);
        windowWebView.addJavascriptInterface(new S3AppNative(this), "s3app");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            windowWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(windowWebView, true);
        }

        RelativeLayout windowContainer = emailNewField.findViewById(R.id.windowContainer);
        windowContainer.addView(windowWebView);
        ApplicationInfoManager.getInstance().addWindowWebViews(windowWebView);

        LinearLayout windowRoot = emailNewField.findViewById(R.id.windowRoot);
        ((WebView.WebViewTransport) resultMsg.obj).setWebView(windowWebView);
        resultMsg.sendToTarget();
        emailNewField.findViewById(R.id.windowCloseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomWebView targetWebView = ApplicationInfoManager.getInstance().getWindowWebViews().get(ApplicationInfoManager.getInstance().getWindowWebViews().size() - 1);
                targetWebView.destroy();
                binding.windowRoot.removeView(emailNewField);
                ApplicationInfoManager.getInstance().deleteWindowViewsLastIndex();
                ApplicationInfoManager.getInstance().deleteWindowWebViewsLastIndex();
            }
        });
        mEmailLinear.addView(emailNewField);
        ApplicationInfoManager.getInstance().addWindowViews(emailNewField);
        windowRoot.setVisibility(View.VISIBLE);
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void setWebView() {
        webView.setWebViewClient(new CustomWebViewClient(this, new OnPageLoadedListener() {
            @Override
            public void onFinish() {
                if (isReady && ApplicationInfoManager.getInstance().getRouteUrls() != null
                        && ApplicationInfoManager.getInstance().getRouteUrls().size() > 0) {
                    List<String> urls = ApplicationInfoManager.getInstance().getRouteUrls();
                    ApplicationInfoManager.getInstance().setRouteUrls(new ArrayList<String>());
                    for (int i = 0; i < urls.size(); i++) {
                        final String url = urls.get(i);
                        webView.post(new Runnable() {
                            @Override
                            public void run() {
                                webView.evaluateJavascript("s3web.route('" + url + "')", null);
                                Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }));
        webView.setWebChromeClient(new CustomChromeClient(this));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new S3AppNative(this), "s3app");
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        WebView.setWebContentsDebuggingEnabled(true);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.getSettings().setGeolocationEnabled(true);
        webView.loadUrl(Constants.BASE_URL);
    }


    public void hideWindowWebView() {
        if (ApplicationInfoManager.getInstance().getWindowViews() != null
                && ApplicationInfoManager.getInstance().getWindowViews().size() > 0) {
            final View view = ApplicationInfoManager.getInstance().getWindowViews().get(ApplicationInfoManager.getInstance().getWindowViews().size() - 1);
            CustomWebView targetWebView = ApplicationInfoManager.getInstance().getWindowWebViews().get(ApplicationInfoManager.getInstance().getWindowWebViews().size() - 1);
            targetWebView.post(new Runnable() {
                @Override
                public void run() {
                    CustomWebView targetWebView = ApplicationInfoManager.getInstance().getWindowWebViews().get(ApplicationInfoManager.getInstance().getWindowWebViews().size() - 1);
                    targetWebView.destroy();
                    binding.windowRoot.removeView(view);
                    ApplicationInfoManager.getInstance().deleteWindowViewsLastIndex();
                    ApplicationInfoManager.getInstance().deleteWindowWebViewsLastIndex();
                }
            });
        }
    }

    public void doubleTabFinished() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && 2000 >= intervalTime) {
            finish();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getBaseContext(), "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleBackPress() {
        if (ApplicationInfoManager.getInstance().getWindowViews() != null
                && ApplicationInfoManager.getInstance().getWindowViews().size() > 0
                && ApplicationInfoManager.getInstance().getWindowWebViews() != null
                && ApplicationInfoManager.getInstance().getWindowWebViews().size() > 0) {
            String historyUrl = "";
            CustomWebView targetWebView = ApplicationInfoManager.getInstance().getWindowWebViews().get(ApplicationInfoManager.getInstance().getWindowWebViews().size() - 1);
            WebBackForwardList mWebBackForwardList = targetWebView.copyBackForwardList();
            if (mWebBackForwardList.getCurrentIndex() > 0)
                historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();

            if (historyUrl == null || historyUrl.equals("") || historyUrl.equals("about:blank")) {
                hideWindowWebView();
            } else {
                if (targetWebView.canGoBack())
                    targetWebView.goBack();
                else
                    hideWindowWebView();
            }
        } else {
            if (binding.webView.canGoBack() && !binding.webView.getUrl().equals(Constants.BASE_URL + "/")) {
                String historyUrl = "";
                WebBackForwardList mWebBackForwardList = binding.webView.copyBackForwardList();
                if (mWebBackForwardList.getCurrentIndex() > 0)
                    historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();

                if (historyUrl == null || historyUrl.equals("") || historyUrl.equals("about:blank")) {
                    binding.webView.loadUrl(Constants.BASE_URL);
                } else {
                    if (historyUrl.split("://").length < 2
                            || historyUrl.split("://")[1] == null)
                        doubleTabFinished();
                    else
                        binding.webView.goBack();
                }
            } else {
                doubleTabFinished();
            }
        }
    }

    public void checkPermission(String origin, GeolocationPermissions.Callback callback) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            callback.invoke(origin, true, false);
        } else {
            this.callback = callback;
            this.origin = origin;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 4000) {
            callback.invoke(origin, true, false);
        }
    }


    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                }
            };
            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you deny permission, you cannot use the function. You can also change it in [Settings] -> [Permissions].")
                    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .check();
        }
    }

    public void setReady() {
        this.isReady = true;
        if (ApplicationInfoManager.getInstance().getRouteUrls() != null
                && ApplicationInfoManager.getInstance().getRouteUrls().size() > 0) {
            List<String> urls = ApplicationInfoManager.getInstance().getRouteUrls();
            ApplicationInfoManager.getInstance().setRouteUrls(new ArrayList<String>());

            for (int i = 0; i < urls.size(); i++) {
                final String url = urls.get(i);
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.evaluateJavascript("s3web.route('" + url + "')", null);
                    }
                });
            }
            ApplicationInfoManager.getInstance().setRouteUrls(new ArrayList<String>());
        }
    }

    public void setStatusBarColor(String color) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor(color));
    }

    public void setPushNotificationUser(String userId) {
        if (userId.isEmpty()) {
            OneSignal.logout();
        } else {
            OneSignal.login(userId);
        }
    }
}
