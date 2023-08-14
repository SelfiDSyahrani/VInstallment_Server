package com.example.vinstallment_server;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Locale;

public class MyService extends Service {
    public MyService() {
    }
    private TextToSpeech textToSpeech;
    private final IMyAidlInterface.Stub binder = new IMyAidlInterface.Stub() {

        @Override
        public void HMinSatu() throws RemoteException {
            sendNotification("Besok adalah jatuh tempo pembayaran cicilan. Pastikan untuk melakukan pembayaran tepat waktu. Terima kasih!");
        }

        @Override
        public void HMinNol() throws RemoteException {
            sendNotification("Hari ini adalah jatuh tempo pembayaran cicilan. Harap segera lakukan pembayaran, Abaikan pesan ini jika anda sudah membayar. Terima kasih!");
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

        @Override
        public void HPlusSatu() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            dpm.setCameraDisabled(adminComponent, true);

            showPopup("Akses kamera dimatikan, segera lakukan pembayaran, untuk mengaktifkan akses kembali.");
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


        @Override
        public void HPlusDua() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            suspendAllAppsExceptAllowedApps(dpm, adminComponent);
        }

        private void suspendAllAppsExceptAllowedApps(DevicePolicyManager dpm, ComponentName adminComponent) {
            String[] allowedPackageNames = new String[]{
                    "com.whatsapp",
                    "com.android.phone",
                    "com.android.settings",
                    "com.android.contacts"
            };

            for (String packageName : allowedPackageNames) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    dpm.setPackagesSuspended(adminComponent, new String[]{packageName}, true);
                }
            }
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
            String[] suspendedPackages = new String[]{
                    "com.whatsapp",
                    "com.android.phone",
                    "com.android.settings",
                    "com.android.contacts"
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    dpm.setPackagesSuspended(adminComponent, suspendedPackages, false);
                }
            }
        }

        @Override
        public void Lunas() throws RemoteException {

        }

        private void playTextToSpeech(Context context, String textToSpeak) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        Locale locale = Locale.US;
                        int result = textToSpeech.setLanguage(locale);
                        HashMap<String, String> params = new HashMap<>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                        textToSpeech.speak(textToSpeak,TextToSpeech.QUEUE_FLUSH, params);
                    }
                }
            });
        }


    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
