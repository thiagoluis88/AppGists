package br.com.thiagoluis.appgists.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import br.com.thiagoluis.appgists.BaseActivity;
import br.com.thiagoluis.appgists.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, GistsActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
