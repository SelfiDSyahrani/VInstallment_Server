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

        btnMinSatu.setOnClickListener(v -> {
            sendNotification("Besok adalah jatuh tempo pembayaran cicilan. Pastikan untuk melakukan pembayaran tepat waktu. Terima kasih!", true);
        });

        btnMinNol.setOnClickListener(v -> {
            sendNotification("Hari ini adalah jatuh tempo pembayaran cicilan. Harap segera lakukan pembayaran, Abaikan pesan ini jika anda sudah membayar. Terima kasih!", true);
        });

        btnPlusSatu.setOnClickListener(v -> {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            dpm.setCameraDisabled(adminComponent, true);
            Toast.makeText(getApplicationContext(), "Akses kamera anda dinonaktifkan", Toast.LENGTH_SHORT).show();

//            showPopup("Akses kamera dimatikan, segera lakukan pembayaran, untuk mengaktifkan akses kembali.");
        });

        btnPlusDua.setOnClickListener(v -> {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            suspendAllAppsExceptAllowedApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Beberapa aplikasi anda dinonaktifkan", Toast.LENGTH_SHORT).show();
        });

        btnPlusTiga.setOnClickListener(v -> {
            Context context = getApplicationContext();
            playTextToSpeech(context, "Silahkan bayar tagihan anda");
        });

        btnBayar.setOnClickListener(v -> {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);
            dpm.setCameraDisabled(adminComponent, false);
            unsuspendApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Akses aplikasi anda kembali diaktifkan", Toast.LENGTH_SHORT).show();

        });

        btnLunas.setOnClickListener(v -> {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            unsuspendApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Akses aplikasi anda kembali diaktifkan", Toast.LENGTH_SHORT).show();

            if (dpm.isAdminActive(adminComponent)) {
//                dpm.removeActiveAdmin(adminComponent);
                dpm.clearDeviceOwnerApp(context.getPackageName());
//                dpm.wipeData(0); // normal reset factory
//                dpm.wipeData(DevicePolicyManager.WIPE_RESET_PROTECTION_DATA); // reset factory + wipe factory reset protection
//                Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
            }

        });

//        Intent intent = new Intent("MyService");
//        intent.setPackage("com.example.vinstallment_server");
//        bindService(intent, mConnection, BIND_AUTO_CREATE);
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

    private void showPopup(String message) {
        Context context = getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Peringatan");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendNotification(message, false);
            }
        });
        builder.show();
    }

    private void sendNotification(String contentText, boolean autocancel) {
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
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(autocancel);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(1, builder.build());
    }

    private void suspendAllAppsExceptAllowedApps(DevicePolicyManager dpm, ComponentName adminComponent) {

        List<String> exemptPackages = Arrays.asList("com.android.settings", "com.android.contacts", "com.android.phone");

        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(0);
        Log.d("suspend", "suspendAllAppsExceptAllowedApps: " + installedPackages);
        for (PackageInfo packageInfo : installedPackages) {
            String packageName = packageInfo.packageName;

            if (!exemptPackages.contains(packageName)) {
                dpm.setPackagesSuspended(adminComponent, new String[]{packageName}, true); // Suspend the app
            }
        }

    }

    private void playTextToSpeech(Context context, String textToSpeak) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("id", "ID");
                    int result = textToSpeech.setLanguage(locale);
                    HashMap<String, String> params = new HashMap<>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                    textToSpeech.speak(textToSpeak,TextToSpeech.QUEUE_FLUSH, params);
                }
            }
        });
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
