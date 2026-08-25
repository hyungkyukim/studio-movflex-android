package kr.movflex.app.vms.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import kr.movflex.app.vms.BaseVM;

public class ActivityVM extends BaseVM {
    private Activity activity;
    public ActivityVM(Activity activity, @Nullable Bundle savedInstanceState) {
        super(activity, savedInstanceState);
        this.activity = activity;
    }

    public ActivityVM(Activity activity) {
        super(activity);
    }

    public Activity getActivity() {
        return activity;
    }
}
