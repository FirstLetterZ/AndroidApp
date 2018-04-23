package com.zpf.appLib.util;

import android.content.DialogInterface;

import com.zpf.appLib.base.BaseDialog;

import java.util.LinkedList;

/**
 * Created by ZPF on 2018/3/22.
 */
public class DialogControlUtil {
    private LifeControl control;
    private BaseDialog showingDialog = null;
    private volatile LinkedList<BaseDialog> dialogList = new LinkedList<>();

    public DialogControlUtil(LifeControl control) {
        this.control = control;
    }

    public void showDialog(BaseDialog dialog) {
        if (!control.isDestroy() && dialog != null && dialog != showingDialog && !dialogList.contains(dialog)) {
            dialog.setFirstListener(getFirstDismissListener());
            if (showingDialog == null) {
                showingDialog = dialog;
                dialog.show();
            } else {
                dialogList.add(dialog);
            }
        }
    }

    private DialogInterface.OnDismissListener getFirstDismissListener() {
        return new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (control.isDestroy()) {
                    dialogList.clear();
                    showingDialog = null;
                } else {
                    BaseDialog nextDialog = dialogList.peekFirst();
                    if (nextDialog != null) {
                        showingDialog = nextDialog;
                        nextDialog.show();
                    } else {
                        showingDialog = null;
                    }
                }
            }
        };
    }

    public void onDestroy() {
        if (showingDialog != null) {
            showingDialog.clearDismissListener();
            showingDialog.dismiss();
            showingDialog = null;
        }
    }

    public interface LifeControl {
        boolean isDestroy();
    }
}
