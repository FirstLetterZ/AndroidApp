package com.zpf.support.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public interface ActivityController {
    void startActivity(Intent intent);

    void startActivity(Intent intent, @Nullable Bundle options);

    void startActivities(Intent[] intents);

    void startActivities(Intent[] intents, @Nullable Bundle options);

    void startActivityForResult(Intent intent, int requestCode);

    void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options);

    void finish();

    void finishWithResult(int resultCode, @Nullable Intent data);

    Context getContext();

    Intent getIntent();

    Activity getCurrentActivity();

}
