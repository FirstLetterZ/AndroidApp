package com.zpf.support.single;

import android.content.Intent;

/**
 * Created by ZPF on 2019/5/15.
 */

public interface OnStackEmptyListener {
    void onEmpty(int requestCode, int resultCode, Intent data);
}
