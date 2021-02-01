package com.zpf.support.network.model;

public class RequestType {
    public final boolean non_null;
    public final boolean check_local;
    public final boolean auto_update;
    public final boolean auto_toast;
    public final boolean ignore_loading;
    public static RequestType DEF_TYPE = new RequestType(true, true, true, true, false);
    public static RequestType NULABLE = new RequestType(false, true, true, true, false);
    public static RequestType NOTOAST = new RequestType(true, true, true, false, false);
    public static RequestType NULABLE_NOTOAST = new RequestType(false, true, true, false, false);

    public RequestType(boolean auto_toast) {
        this.non_null = true;
        this.check_local = false;
        this.auto_update = false;
        this.auto_toast = auto_toast;
        this.ignore_loading = false;
    }

    public RequestType(boolean non_null, boolean auto_toast) {
        this.non_null = non_null;
        this.check_local = false;
        this.auto_update = false;
        this.auto_toast = auto_toast;
        this.ignore_loading = false;
    }

    public RequestType(boolean non_null, boolean auto_toast, boolean ignore_loading) {
        this.non_null = non_null;
        this.check_local = false;
        this.auto_update = false;
        this.auto_toast = auto_toast;
        this.ignore_loading = ignore_loading;
    }

    public RequestType(boolean non_null, boolean check_local, boolean auto_update, boolean auto_toast, boolean ignore_loading) {
        this.non_null = non_null;
        this.check_local = check_local;
        this.auto_update = auto_update;
        this.auto_toast = auto_toast;
        this.ignore_loading = ignore_loading;
    }
}
