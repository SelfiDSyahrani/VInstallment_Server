package com.example.vinstallment_server;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.OnReceiveContentListener;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context.getApplicationContext(), "Device-Owner Mode Disable", Toast.LENGTH_SHORT).show();

    }
}



