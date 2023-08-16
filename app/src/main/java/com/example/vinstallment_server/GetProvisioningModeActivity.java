package com.example.vinstallment_server;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_MODE;
import static android.app.admin.DevicePolicyManager.PROVISIONING_MODE_FULLY_MANAGED_DEVICE;

import androidx.appcompat.app.AppCompatActivity;

public class GetProvisioningModeActivity extends AppCompatActivity {

    private Button startEnrollmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_provisioning_mode);

        startEnrollmentButton = findViewById(R.id.btn_start_enrollment);
        startEnrollmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFullyManagedProvisioning();
            }
        });

    }

    private void startFullyManagedProvisioning() {
        final Intent provisioningIntent = new Intent();
        provisioningIntent.putExtra(EXTRA_PROVISIONING_MODE, PROVISIONING_MODE_FULLY_MANAGED_DEVICE);
        setResult(RESULT_OK, provisioningIntent);
        finish();

    }

}
