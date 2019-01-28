package com.zpf.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zpf.app.R;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void back(View view) {
        finish();
    }
}
