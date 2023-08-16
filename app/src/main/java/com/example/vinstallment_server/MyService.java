package com.example.vinstallment_server;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MyService extends Service {
    public MyService() {
    }

    private TextToSpeech textToSpeech;
    private final IMyAidlInterface.Stub binder = new IMyAidlInterface.Stub() {


        @Override
        public void HMinSatu() throws RemoteException {
            sendNotification("Besok adalah jatuh tempo pembayaran cicilan. Pastikan untuk melakukan pembayaran tepat waktu. Terima kasih!", true);
        }

        @Override
        public void HMinNol() throws RemoteException {
            sendNotification("Hari ini adalah jatuh tempo pembayaran cicilan. Harap segera lakukan pembayaran, Abaikan pesan ini jika anda sudah membayar. Terima kasih!", true);
        }

        @Override
        public void HPlusSatu() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            dpm.setCameraDisabled(adminComponent, true);
            Toast.makeText(getApplicationContext(), "Akses kamera anda dinonaktifkan", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void HPlusDua() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            suspendAllAppsExceptAllowedApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Beberapa aplikasi anda dinonaktifkan", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void HPlusTiga() throws RemoteException {
            Context context = getApplicationContext();
            playTextToSpeech(context, "Silahkan bayar tagihan anda");
        }

        @Override
        public void Bayar() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);
            dpm.setCameraDisabled(adminComponent, false);
            unsuspendApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Akses aplikasi anda kembali diaktifkan", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void Lunas() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            unsuspendApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Akses aplikasi anda kembali diaktifkan", Toast.LENGTH_SHORT).show();

            if (dpm.isAdminActive(adminComponent)) {
//                dpm.clearDeviceOwnerApp(context.getPackageName());
                dpm.wipeData(0); // normal reset factory
            }
        }
    };

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
