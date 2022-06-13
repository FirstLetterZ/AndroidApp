package com.zpf.frame;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.OnActivityResultListener;

public interface IActivityStartController {

    void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options);

    void startActivityForResult(@NonNull Intent intent, @NonNull OnActivityResultListener listener);

    void finish();

    void finishWithResult(int resultCode, @Nullable Intent data);

}
