package kr.movflex.app.webview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import kr.movflex.app.activity.MainActivity;
import kr.movflex.app.common.Constants;

public class CustomChromeClient extends WebChromeClient {
    private MainActivity mainActivity;

    public CustomChromeClient(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        mainActivity.openWindowWebView(resultMsg);
        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
        mainActivity.hideWindowWebView();
        super.onCloseWindow(window);
    }

    //웹에서 띄우는 팝업 창을 웹뷰에서 보여주기 위해 사용
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (result != null)
                            result.confirm();
                    }
                })
                .create();
        dialog.setCancelable(false);
        dialog.show();
        return true;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        mainActivity.checkPermission(origin, callback);
    }
}
