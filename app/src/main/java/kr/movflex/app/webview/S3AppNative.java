package kr.movflex.app.webview;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import androidx.browser.customtabs.CustomTabsIntent;

import kr.movflex.app.activity.MainActivity;


public class S3AppNative {
    private MainActivity activity;

    public S3AppNative(MainActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void openBrowser(String url) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (url == null || url.equals(""))
                    return;

                Intent i = new Intent();

                if (url.startsWith("tel:")) {
                    i.setAction(Intent.ACTION_DIAL);
                } else if (url.startsWith("mailto:")) {
                    i.setAction(Intent.ACTION_VIEW);
                } else if (url.startsWith("sms:")) {
                    i.setAction(Intent.ACTION_SENDTO);
                } else if (url.startsWith("instagram")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                    Intent instagram = null;
                    if (intent == null) {
                        String test = "https://www.instagram.com/";
                        String link = null;
                        if (url.contains("user")) {
                            String username = uri.getQueryParameter("username");
                            link = test + username;
                        } else if (url.contains("media")) {
                            String id = uri.getQueryParameter("id");
                            link = test + "p/" + id;
                        }
                        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(link)));
                        return;
                    } else {
                        instagram = new Intent(Intent.ACTION_VIEW, uri);
                    }
                    try {
                        activity.startActivity(instagram);
                        return;
                    } catch (ActivityNotFoundException e) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url)));
                        return;
                    }
                } else {
                    i.setPackage("com.android.chrome");
                    i.setAction(Intent.ACTION_VIEW);
                }
                i.setData(Uri.parse(url));
                activity.startActivity(i);
            }
        });
    }


    @JavascriptInterface
    public void openInAppBrowser(final String url) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                if (intent != null) {
                    customTabsIntent.intent.setPackage("com.android.chrome");
                    customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    customTabsIntent.launchUrl(activity, Uri.parse(url));
                } else {
                    customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    customTabsIntent.launchUrl(activity.getApplicationContext(), Uri.parse(url));
                }
            }
        });
    }

    @JavascriptInterface
    public void setRouterReady() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                activity.setReady();
            }
        });
    }

    @JavascriptInterface
    public void setPushNotificationUser(String userId) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                activity.setPushNotificationUser(userId);
            }
        });
    }

    @JavascriptInterface
    public void shareText(String text) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                activity.startActivity(Intent.createChooser(sharingIntent, "텍스트 공유"));
            }
        });
    }
}
