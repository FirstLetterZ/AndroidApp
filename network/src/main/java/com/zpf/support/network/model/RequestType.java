package com.zpf.support.network.model;

public class RequestType {
    boolean non_null;
    boolean check_local;
    boolean auto_update;
    boolean auto_toast;
    boolean ignore_loading;
    public static RequestType DEF_TYPE = new RequestType(true, true, true, true, false);

    RequestType(boolean auto_toast) {
        this.non_null = true;
        this.check_local = false;
        this.auto_update = false;
        this.auto_toast = auto_toast;
        this.ignore_loading = false;
    }

    RequestType(boolean non_null, boolean auto_toast) {
        this.non_null = non_null;
        this.check_local = false;
        this.auto_update = false;
        this.auto_toast = auto_toast;
        this.ignore_loading = false;
    }

    RequestType(boolean non_null, boolean auto_toast, boolean ignore_loading) {
        this.non_null = non_null;
        this.check_local = false;
        this.auto_update = false;
        this.auto_toast = auto_toast;
        this.ignore_loading = ignore_loading;
    }

    RequestType(boolean non_null, boolean check_local, boolean auto_update, boolean auto_toast, boolean ignore_loading) {
        this.non_null = non_null;
        this.check_local = check_local;
        this.auto_update = auto_update;
        this.auto_toast = auto_toast;
        this.ignore_loading = ignore_loading;
    }
}
