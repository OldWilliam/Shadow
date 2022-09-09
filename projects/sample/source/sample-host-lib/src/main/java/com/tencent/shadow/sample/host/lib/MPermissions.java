package com.tencent.shadow.sample.host.lib;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class MPermissions {


    public static MPermissions getInstance() {
        return new MPermissions();
    }

    public void requestPermission(Context context){
        Activity activity = findActivity(context);
        if (isDenied()) {
            new AlertDialog.Builder(activity)
                    .setTitle("权限 Title")
                    .setMessage("权限 Msg")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("go", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();
        }
    }

    private boolean isDenied() {
        return true;
    }

    /**
     * 寻找上下文中的 Activity 对象
     */
    static Activity findActivity(Context context) {
        do {
            if (context instanceof Activity) {
                return (Activity) context;
            } else if (context instanceof ContextWrapper){
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        } while (context != null);
        return null;
    }
}
