package com.example.vinstallment_server;



import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {
    private IMyAidlInterface iMyAidlService;

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

        SwitchMaterial switchMinSatu = findViewById(R.id.switchHMinus1);
        SwitchMaterial switchMinNol = findViewById(R.id.switchHZero);
        SwitchMaterial switchPlusSatu = findViewById(R.id.switchHPlus1);
        SwitchMaterial switchPlusDua = findViewById(R.id.switchHPlus2);
        SwitchMaterial switchPlusTiga = findViewById(R.id.switchHPlus3);
        btnBayar = findViewById(R.id.buttonBayar);
        btnLunas = findViewById(R.id.buttonLunas);

        Intent intent = new Intent("MyService");
        intent.setPackage("com.example.vinstallment_server");
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        switchMinSatu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    iMyAidlService.HMinSatu();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        switchMinNol.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    iMyAidlService.HMinNol();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        switchPlusSatu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    iMyAidlService.HPlusSatu();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        switchPlusDua.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    iMyAidlService.HPlusDua();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        switchPlusTiga.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    iMyAidlService.HPlusTiga();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
