package com.zpf.support.model;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SimpleActivityResultContract extends ActivityResultContract<Intent, Intent> {
    private int resultCode;
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Intent input) {
        return input;
    }

    @Override
    public Intent parseResult(int resultCode, @Nullable Intent intent) {
        this.resultCode=resultCode;
        return intent;
    }

    public int getResultCode() {
        return resultCode;
    }
}
