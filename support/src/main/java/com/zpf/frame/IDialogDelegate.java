package com.zpf.frame;

import android.app.Activity;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IDialogDelegate {
    Dialog createDialog(@NonNull Activity activity, @Nullable Object params, int type);
}