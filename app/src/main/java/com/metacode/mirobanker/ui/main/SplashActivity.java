package com.metacode.mirobanker.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.common.data.DataBufferObserver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.metacode.mirobanker.R;
import com.metacode.mirobanker.ui.auth.LoginActivity;
import com.metacode.mirobanker.ui.base.BaseActivity;
import com.metacode.mirobanker.ui.list.ListActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    private int mSplashelayTimeSeconds;
    private Handler mHandler;
    private Runnable mSplashDelayRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSplashelayTimeSeconds = getResources().getInteger(R.integer.splash_screen_delay_time_seconds);
        mHandler = new Handler();
        mSplashDelayRunnable = () -> {
            boolean loggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
            startActivity(new Intent(this, loggedIn ? ListActivity.class : LoginActivity.class));
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mSplashDelayRunnable, TimeUnit.SECONDS.toMillis(mSplashelayTimeSeconds));
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mSplashDelayRunnable);
        super.onStop();
    }

}
