package com.example.vinstallment_server;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        // add setAppUninstallBlocked to disable uninstall client app
        // setAppUninstallBlocked(context, "com.example.vinstallment_test", true);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context.getApplicationContext(), "Device-Owner Mode Disable", Toast.LENGTH_SHORT).show();
        // after DEVICE OWNER disable, client app and server app (dpc) is able to be uninstall
        // unblock uninstall restriction
//        setAppUninstallBlocked(context, "com.example.vinstallment_test", false);
    }

}



