package kr.movflex.app;

import android.app.Application;

import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        patchEOFException();
        // Enable verbose logging to debug issues (remove in production)
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // Replace with your 36-character App ID from Dashboard > Settings > Keys & IDs
        OneSignal.initWithContext(this, "879fa346-5239-45e5-a9a8-25ff52f5f1ef");

        // Prompt user for push notification permission
        // In production, consider using an in-app message instead for better opt-in rates
        OneSignal.getNotifications().requestPermission(false, Continue.none());
    }

    private void patchEOFException() {
        System.setProperty("http.keepAlive", "false");
    }
}
