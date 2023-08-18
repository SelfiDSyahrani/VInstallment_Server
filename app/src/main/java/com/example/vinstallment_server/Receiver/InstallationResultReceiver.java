package com.example.vinstallment_server.Receiver;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;
import android.widget.Toast;

public class InstallationResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int sessionId = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1);
            if (sessionId == PackageInstaller.SessionInfo.INVALID_ID) {
                Log.d(TAG, "invalid SessionId ");
                return;
            }
        }

        String packageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME);
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);

        if (status == PackageInstaller.STATUS_SUCCESS) {
            Toast.makeText(context, "Installaion success", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Installation Failed");
            // Installation failure
            // Handle failure if needed
        }
    }
}