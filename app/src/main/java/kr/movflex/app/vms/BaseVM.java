package kr.movflex.app.vms;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;

public class BaseVM extends BaseObservable {
    protected Context context;
    protected Bundle savedInstanceState;

    public BaseVM(Context context, @Nullable Bundle savedInstanceState) {
        this.context = context;
        this.savedInstanceState = savedInstanceState;
    }

    public BaseVM(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }
}
