package com.example.vinstallment_server;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private IMyAidlInterface iMyAidlService;
    private Button btnMinSatu, btnMinNol, btnPlusSatu, btnPlusDua, btnPlusTiga, btnBayar, btnLunas;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMyAidlService = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMyAidlService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMinSatu = findViewById(R.id.buttonHMinus1);
        btnMinNol = findViewById(R.id.buttonHZero);
        btnPlusSatu = findViewById(R.id.buttonHPlus1);
        btnPlusDua = findViewById(R.id.buttonHPlus2);
        btnPlusTiga = findViewById(R.id.buttonHPlus3);
        btnBayar = findViewById(R.id.buttonBayar);
        btnLunas = findViewById(R.id.buttonLunas);

        Intent intent = new Intent("MyService");
        intent.setPackage("com.example.vinstallment_server");
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        btnMinSatu.setOnClickListener(v -> {
            try {
                iMyAidlService.HMinSatu();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        btnMinNol.setOnClickListener(v -> {
            try {
                iMyAidlService.HMinNol();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        btnPlusSatu.setOnClickListener(v -> {
            try {
                iMyAidlService.HPlusSatu();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        btnPlusDua.setOnClickListener(v -> {
            try {
                iMyAidlService.HPlusDua();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        btnPlusTiga.setOnClickListener(v -> {
            try {
                iMyAidlService.HPlusTiga();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        btnBayar.setOnClickListener(v -> {
            try {
                iMyAidlService.Bayar();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        btnLunas.setOnClickListener(v -> {
            try {
                iMyAidlService.Lunas();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void unsuspendApps(DevicePolicyManager dpm, ComponentName adminComponent) {
        List<String> exemptPackages = Arrays.asList("com.android.settings", "com.android.contacts", "com.android.phone");

        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);

        for (PackageInfo packageInfo : installedPackages) {
            String packageName = packageInfo.packageName;

            if (!exemptPackages.contains(packageName)) {
                dpm.setPackagesSuspended(adminComponent, new String[]{packageName}, false); // Unsuspend the app
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
