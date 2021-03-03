package org.ituns.google.chrome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private ChromeClient chromeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chromeClient = new ChromeClient(this);
    }

    public void loadUrl(View view) {
        try {
            chromeClient.openChrome("http://www.baidu.com/");
        } catch (ChromeException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chromeClient.release();
    }
}