package com.example.vinstallment_server;

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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

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

        btnMinSatu.setOnClickListener(v -> {
            sendNotification("Besok adalah jatuh tempo pembayaran cicilan. Pastikan untuk melakukan pembayaran tepat waktu. Terima kasih!");
        });

        btnMinNol.setOnClickListener(v -> {
            sendNotification("Hari ini adalah jatuh tempo pembayaran cicilan. Harap segera lakukan pembayaran, Abaikan pesan ini jika anda sudah membayar. Terima kasih!");
        });

        btnPlusSatu.setOnClickListener(v -> {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            dpm.setCameraDisabled(adminComponent, true);

            showPopup("Akses kamera dimatikan, segera lakukan pembayaran, untuk mengaktifkan akses kembali.");
        });
        btnBayar.setOnClickListener(v -> {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            dpm.setCameraDisabled(adminComponent, false);
        });

//        Intent intent = new Intent("MyService");
//        intent.setPackage("com.example.vinstallment_server");
//        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }
    private void showPopup(String message) {
        // Buat dialog atau AlertDialog untuk menampilkan popup
        Context context = getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Peringatan");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Aksi yang dilakukan ketika pengguna menekan tombol OK
            }
        });
        builder.show();
    }

    private void sendNotification(String contentText) {
        Context context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle("VInstallment")
                .setContentText("Pemberitahuan: " + contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(1, builder.build());
    }

//    private void executeMethodSafely(RemoteMethodExecutor executor) {
//        if (iMyAidlService != null) {
//            try {
//                executor.execute();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private interface RemoteMethodExecutor {
        void execute() throws RemoteException;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
