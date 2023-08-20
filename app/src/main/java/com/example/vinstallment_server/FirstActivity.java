package com.example.vinstallment_server;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Get DevicePolicyManager
        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);

        // Check if the app is the device owner
        if (manager.isDeviceOwnerApp(getPackageName())) {
//            Log.d(TAG, "onCreate: ");
            Toast.makeText(getApplicationContext(), "App is the device owner", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            // App is not the device owner
            Toast.makeText(getApplicationContext(), "App is not the device owner", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FirstActivity.this, GetProvisioningModeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
