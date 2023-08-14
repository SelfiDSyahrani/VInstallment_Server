package com.example.vinstallment_server;

import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GetProvisioningModeActivity extends AppCompatActivity {

    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;
    private Button startEnrollmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_provisioning_mode);
        PackageManager pm = getPackageManager();
        startEnrollmentButton = findViewById(R.id.btn_start_enrollment);
        startEnrollmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeviceProvisioning();
            }
        });
    }

    private void startDeviceProvisioning() {

        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(GetProvisioningModeActivity.this, MyDeviceAdminReceiver.class);
        if (!dpm.isDeviceOwnerApp(getPackageName())) {
            // Set up a provisioning intent
            Intent provisioningIntent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_DEVICE);
            provisioningIntent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME, adminComponent);
            provisioningIntent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_MODE, DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE);

            // Start the provisioning process
            startActivityForResult(provisioningIntent, REQUEST_PROVISION_MANAGED_PROFILE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if this is the result of the provisioning activity
        if (requestCode == REQUEST_PROVISION_MANAGED_PROFILE) {
            // If provisioning was successful, the result code is
            // Activity.RESULT_OK
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(),  "Provisioning done.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),  "Provisioning failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // This is the result of some other activity. Call the superclass.
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}


