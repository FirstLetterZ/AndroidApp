package com.zpf.app.activity.finger;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.zpf.tool.FileUtil;
import com.zpf.tool.MainHandler;

/**
 * Created by ZPF on 2019/2/12.
 */
public class FingerUtil {
    FingerprintManager mManager;
    private int NORMAL = 0;
    private int AUTH_START = 0;
    private int AUTH_CANCEL = 0;
    private int AUTH_FAIL = 0;
    private int AUTH_ERROR = 0;
    private int AUTH_SUCCESS = 0;


    private int NO_MANAGER = 0;
    private int NOT_SUPPORT = 0;
    private int NO_DATA = 0;

    private volatile int mRetryTime;
    private int mMaxRetryTime;
    private int mState;
    private FingerprintManager.CryptoObject mCryptoObject;

    private static FingerUtil mInstance;

    public static FingerUtil get() {
        if (mInstance == null) {
            synchronized (FileUtil.class) {
                if (mInstance == null) {
                    mInstance = new FingerUtil();
                }
            }
        }
        return mInstance;
    }

    public static boolean init(Context context) {
        if (get().mManager != null) {
            return true;
        }
        if (context == null) {
            return false;
        }
        get().mManager = getFingerprintManager(context);
        return get().mManager != null;
    }

    public FingerUtil() {
        new CryptoObjectHelper(new CryptoObjectHelper.OnPreparedListener() {
            @Override
            public void onPrepared(FingerprintManager.CryptoObject cryptoObject, int stateCode) {
                mCryptoObject = cryptoObject;
            }
        });
    }

    private android.os.CancellationSignal mCancellationSignal;

    public static FingerprintManager getFingerprintManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                    return context.getSystemService(FingerprintManager.class);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void authenticate(final FingerprintAuthListener listener) {
        authenticate(listener, false);
    }

    public void authenticate(final FingerprintAuthListener listener, boolean isRetry) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (mManager == null) {
            mState = NO_MANAGER;
            return;
        }
        if (!mManager.isHardwareDetected()) {
            mState = NOT_SUPPORT;
            return;
        }
        if (!mManager.hasEnrolledFingerprints()) {
            mState = NO_DATA;
            return;
        }
        if (mCancellationSignal == null) {
            mCancellationSignal = new android.os.CancellationSignal();
        }
        if (isRetry) {
            mRetryTime = 0;
        }
        mState = AUTH_START;
        mManager.authenticate(mCryptoObject, mCancellationSignal, 0, authCallback, null);
    }

    private Runnable mRetryRunnable = new Runnable() {
        @Override
        public void run() {
            authenticate(authListener);
        }
    };

    private FingerprintManager.AuthenticationCallback authCallback = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
        }
    }

    private void retry(final FingerprintAuthListener listener, long delay) {
        MainHandler.get().removeCallbacks(mRetryRunnable);
        if (delay < 10) {
            MainHandler.get().post(mRetryRunnable);
        } else {
            MainHandler.get().postDelayed(mRetryRunnable, delay);
        }
    }

    public void cancel() {
        if (mCancellationSignal != null && mState != AUTH_CANCEL) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
        mState = AUTH_CANCEL;
    }
}
