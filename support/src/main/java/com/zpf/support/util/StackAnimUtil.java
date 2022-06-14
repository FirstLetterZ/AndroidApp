package com.zpf.support.util;

import android.app.Activity;

import com.zpf.support.R;

public class StackAnimUtil {

    public static void onPush(Activity activity, @StackInAnimType int type) {
        switch (type) {
            case StackInAnimType.IN_BOTTOM:
                activity.overridePendingTransition(R.anim.in_from_bottom, 0);
                break;
            case StackInAnimType.IN_LEFT_OUT_RIGHT:
                activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case StackInAnimType.IN_LEFT:
                activity.overridePendingTransition(R.anim.in_from_left, 0);
                break;
            case StackInAnimType.IN_RIGHT_OUT_LEFT:
                activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_RIGHT:
                activity.overridePendingTransition(R.anim.in_from_right, 0);
                break;
            case StackInAnimType.IN_ZOOM_OUT_ZOOM:
                activity.overridePendingTransition(R.anim.in_center_zoom, R.anim.out_center_zoom);
                break;
            case StackInAnimType.IN_ZOOM:
                activity.overridePendingTransition(R.anim.in_center_zoom, 0);
                break;
            case StackInAnimType.NONE:
                activity.overridePendingTransition(0, 0);
                break;
        }
    }

    public static void onPoll(Activity activity, @StackInAnimType int type) {
        switch (type) {
            case StackInAnimType.IN_BOTTOM:
                activity.overridePendingTransition(0, R.anim.out_to_bottom);
                break;
            case StackInAnimType.IN_LEFT_OUT_RIGHT:
                activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_LEFT:
                activity.overridePendingTransition(0, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_RIGHT_OUT_LEFT:
                activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case StackInAnimType.IN_RIGHT:
                activity.overridePendingTransition(0, R.anim.out_to_right);
                break;
            case StackInAnimType.IN_ZOOM_OUT_ZOOM:
                activity.overridePendingTransition(R.anim.in_center_zoom, R.anim.out_center_zoom);
                break;
            case StackInAnimType.IN_ZOOM:
                activity.overridePendingTransition(0, R.anim.out_center_zoom);
                break;
            case StackInAnimType.NONE:
                activity.overridePendingTransition(0, 0);
                break;
        }
    }


    public static void onPush(android.app.FragmentTransaction transaction, @StackInAnimType int type) {
        switch (type) {
            case StackInAnimType.IN_BOTTOM:
                transaction.setCustomAnimations(R.animator.in_from_bottom, 0);
                break;
            case StackInAnimType.IN_LEFT_OUT_RIGHT:
                transaction.setCustomAnimations(R.animator.in_from_left, R.animator.out_to_right);
                break;
            case StackInAnimType.IN_LEFT:
                transaction.setCustomAnimations(R.animator.in_from_left, 0);
                break;
            case StackInAnimType.IN_RIGHT_OUT_LEFT:
                transaction.setCustomAnimations(R.animator.in_from_right, R.animator.out_to_left);
                break;
            case StackInAnimType.IN_RIGHT:
                transaction.setCustomAnimations(R.animator.in_from_right, 0);
                break;
            case StackInAnimType.IN_ZOOM_OUT_ZOOM:
                transaction.setCustomAnimations(R.animator.in_center_zoom, R.animator.out_center_zoom);
                break;
            case StackInAnimType.IN_ZOOM:
                transaction.setCustomAnimations(R.animator.in_from_bottom, 0);
                break;
            case StackInAnimType.NONE:
                transaction.setCustomAnimations(0, 0);
                break;
        }
    }

    public static void onPoll(android.app.FragmentTransaction transaction, @StackInAnimType int type) {
        switch (type) {
            case StackInAnimType.IN_BOTTOM:
                transaction.setCustomAnimations(0, R.animator.out_to_bottom);
                break;
            case StackInAnimType.IN_LEFT_OUT_RIGHT:
                transaction.setCustomAnimations(R.animator.in_from_right, R.animator.out_to_left);
                break;
            case StackInAnimType.IN_LEFT:
                transaction.setCustomAnimations(0, R.animator.out_to_left);
                break;
            case StackInAnimType.IN_RIGHT_OUT_LEFT:
                transaction.setCustomAnimations(R.animator.in_from_left, R.animator.out_to_right);
                break;
            case StackInAnimType.IN_RIGHT:
                transaction.setCustomAnimations(0, R.animator.in_from_left);
                break;
            case StackInAnimType.IN_ZOOM_OUT_ZOOM:
                transaction.setCustomAnimations(0, R.animator.out_center_zoom);
                break;
            case StackInAnimType.IN_ZOOM:
                transaction.setCustomAnimations(R.animator.in_center_zoom, R.animator.out_center_zoom);
                break;
            case StackInAnimType.NONE:
                transaction.setCustomAnimations(0, 0);
                break;
        }
    }


    public static void onPush(androidx.fragment.app.FragmentTransaction transaction, @StackInAnimType int type) {
        switch (type) {
            case StackInAnimType.IN_BOTTOM:
                transaction.setCustomAnimations(R.anim.in_from_bottom, 0);
                break;
            case StackInAnimType.IN_LEFT_OUT_RIGHT:
                transaction.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case StackInAnimType.IN_LEFT:
                transaction.setCustomAnimations(R.anim.in_from_left, 0);
                break;
            case StackInAnimType.IN_RIGHT_OUT_LEFT:
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_RIGHT:
                transaction.setCustomAnimations(R.anim.in_from_right, 0);
                break;
            case StackInAnimType.IN_ZOOM_OUT_ZOOM:
                transaction.setCustomAnimations(R.anim.in_center_zoom, R.anim.out_center_zoom);
                break;
            case StackInAnimType.IN_ZOOM:
                transaction.setCustomAnimations(R.anim.in_from_bottom, 0);
                break;
            case StackInAnimType.NONE:
                transaction.setCustomAnimations(0, 0);
                break;
        }
    }

    public static void onPoll(androidx.fragment.app.FragmentTransaction transaction, @StackInAnimType int type) {
        switch (type) {
            case StackInAnimType.IN_BOTTOM:
                transaction.setCustomAnimations(0, R.anim.out_to_bottom);
                break;
            case StackInAnimType.IN_LEFT_OUT_RIGHT:
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_LEFT:
                transaction.setCustomAnimations(0, R.anim.out_to_left);
                break;
            case StackInAnimType.IN_RIGHT_OUT_LEFT:
                transaction.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case StackInAnimType.IN_RIGHT:
                transaction.setCustomAnimations(0, R.anim.in_from_left);
                break;
            case StackInAnimType.IN_ZOOM_OUT_ZOOM:
                transaction.setCustomAnimations(0, R.anim.out_center_zoom);
                break;
            case StackInAnimType.IN_ZOOM:
                transaction.setCustomAnimations(0, R.anim.out_center_zoom);
                break;
            case StackInAnimType.NONE:
                transaction.setCustomAnimations(0, 0);
                break;
        }
    }

}
