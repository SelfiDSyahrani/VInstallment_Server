package com.example.vinstallment_server;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREFERENCES_NAME = "myPreference";
    private static final String KEY_1 = "1";
    private static final String KEY_2 = "2";
    private static final String KEY_3 = "3";

    private IMyAidlInterface iMyAidlService;
    SharedPreferences sharedPreferences;


    Button btnBayar, btnLunas;
    private final ServiceConnection mConnection = new ServiceConnection() {
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

        Button switchMinSatu = findViewById(R.id.switchHMinus1);
        Button switchMinNol = findViewById(R.id.switchHZero);
        Button switchPlusSatu = findViewById(R.id.switchHPlus1);
        Button switchPlusDua = findViewById(R.id.switchHPlus2);
        Button switchPlusTiga = findViewById(R.id.switchHPlus3);
        btnBayar = findViewById(R.id.buttonBayar);
        btnLunas = findViewById(R.id.buttonLunas);

        Intent intent = new Intent("MyService");
        intent.setPackage("com.example.vinstallment_server");
        bindService(intent, mConnection, BIND_AUTO_CREATE);


        switchMinSatu.setOnClickListener(v -> {
            try {
                iMyAidlService.HMinSatu();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        switchMinNol.setOnClickListener(v -> {
            try {
                iMyAidlService.HMinNol();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        switchPlusSatu.setOnClickListener(v -> {
            try {
                iMyAidlService.HPlusSatu();
                sendDataToApp2("Satu");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });

        switchPlusDua.setOnClickListener(v -> {
            try {
                iMyAidlService.HPlusDua();
                sendDataToApp2("Dua");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        switchPlusTiga.setOnClickListener(v -> {
            try {
                iMyAidlService.HPlusTiga();
                sendDataToApp2("Tiga");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        btnBayar.setOnClickListener(v -> {
            try {
                iMyAidlService.Bayar();
                sendDataToApp2("Bayar");
//                switchPlusSatu.setEnabled(true);
//                switchPlusDua.setEnabled(true);
//                switchPlusTiga.setEnabled(true);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        btnLunas.setOnClickListener(v -> {
            try {
                iMyAidlService.Lunas();
                switchPlusSatu.setEnabled(true);
                switchPlusDua.setEnabled(true);
                switchPlusTiga.setEnabled(true);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void sendDataToApp2(String target) {
        Intent intent = new Intent();
        intent.setAction("com.example.vinstallment_test.ACTION_SEND_DATA");
        intent.putExtra("data", "Aktif");
        intent.putExtra("target", target);
        intent.setPackage("com.example.vinstallment_test");
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
