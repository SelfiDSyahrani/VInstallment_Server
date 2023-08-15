package com.example.vinstallment_server;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.OnReceiveContentListener;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
         Intent ActivityIntent = new Intent(context, FinalizedActivity.class);
         ActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         context.startActivity(ActivityIntent);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context.getApplicationContext(), "berhasil di-disable", Toast.LENGTH_SHORT).show();
    }
}



