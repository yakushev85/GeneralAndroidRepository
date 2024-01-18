package com.alex.yakushev.app.soblocks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String AD_APP_ID =  "ca-app-pub-XXXXX~XXXXX";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findViewById(R.id.btnStartGame).setOnClickListener(this);
        this.findViewById(R.id.btnScore).setOnClickListener(this);

        MobileAds.initialize(getApplicationContext(), AD_APP_ID);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AD_TEST_DEVICE_ID)
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnStartGame) {
            Intent intentStartGame = new Intent(this, GameActivity.class);
            startActivity(intentStartGame);
        } else if (view.getId() == R.id.btnScore) {
            Intent intentScoreGame = new Intent(this, ScoreActivity.class);
            startActivity(intentScoreGame);
        }
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
