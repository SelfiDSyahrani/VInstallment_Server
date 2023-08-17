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
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MyService extends Service {
    private String textToSpeakLagi;

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
//            PackageManager pm = getPackageManager();
//
//            PackageManager pmgr = getPackageManager();
//            List<PackageInfo> installedPackages = pmgr.getInstalledPackages(0);
//
//            for (PackageInfo packageInfo : installedPackages) {
//                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
//                    String packageName = packageInfo.packageName;
//                    Log.d("UserAppPackage", "User App Package Name: " + packageName);
//                }
//            }
        }

        @Override
        public void HPlusSatu() throws RemoteException {
            sendNotification("Anda sudah terlambat satu hari. Segera bayar cicilan Anda.",false);
//            Intent intent = new Intent();
//            intent.setAction("com.example.vinstallment_test.ACTION_UPDATE_TEXT");
//            intent.putExtra("data", "aktif");
//            intent.putExtra("target", "HPlusSatu");
//            intent.setPackage("com.example.vinstallment_test"); // Replace with app2's package name
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);

//            SharedPreferences prefs = getSharedPreferences("demopref",
//                    Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString("Aktif", "Aktif");
//            editor.apply();

        }

        @Override
        public void HPlusDua() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            dpm.setCameraDisabled(adminComponent, true);
            Toast.makeText(getApplicationContext(), "Akses kamera anda dinonaktifkan", Toast.LENGTH_SHORT).show();

//            Intent intent = new Intent();
//            intent.setAction("com.example.vinstallment_test.ACTION_UPDATE_TEXT");
//            intent.putExtra("data", "aktif");
//            intent.putExtra("target", "HPlusDua");
//            intent.setPackage("com.example.vinstallment_test");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);

        }

        @Override
        public void HPlusTiga() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);
            suspendAllAppsExceptAllowedApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Beberapa aplikasi anda dinonaktifkan", Toast.LENGTH_SHORT).show();
            playTextToSpeech(context, "Please proceed with your payment");

//            Intent intent = new Intent();
//            intent.setAction("com.example.vinstallment_test.ACTION_UPDATE_TEXT");
//            intent.putExtra("data", "aktif");
//            intent.putExtra("target", "HPlusTiga");
//            intent.setPackage("com.example.vinstallment_test"); // Replace with app2's package name
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);

        }

        @Override
        public void Bayar() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);
            dpm.setCameraDisabled(adminComponent, false);
            unsuspendApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Akses aplikasi anda kembali diaktifkan", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
//            intent.setAction("com.example.vinstallment_test.ACTION_UPDATE_TEXT");
//            intent.putExtra("data", "aktif");
//            intent.putExtra("target", "Bayar");
//            intent.setPackage("com.example.vinstallment_test");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);

        }

        @Override
        public void Lunas() throws RemoteException {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            Context context = getApplicationContext();
            ComponentName adminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

            unsuspendApps(dpm, adminComponent);
            Toast.makeText(getApplicationContext(), "Akses aplikasi anda kembali diaktifkan", Toast.LENGTH_SHORT).show();

            if (dpm.isAdminActive(adminComponent)) {
                dpm.clearDeviceOwnerApp(context.getPackageName());
//                dpm.wipeData(0); // normal reset factory

            }
        }

        @Override
        public String getValueFromSharedPreferences(String key) throws RemoteException {
            SharedPreferences sharedPreferences = getSharedPreferences("myPreference", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key,"Aktif");
            editor.apply();
            return sharedPreferences.getString(key, "Tidak Aktif");
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
        textToSpeakLagi = textToSpeak;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("id", "ID");
                    int result = textToSpeech.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Locale englishLocale = new Locale("en", "US");
                        textToSpeech.setLanguage(englishLocale);
                        textToSpeakLagi = "Please proceed with your payment";
                    }
                    HashMap<String, String> params = new HashMap<>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                    textToSpeech.speak(textToSpeak,TextToSpeech.QUEUE_FLUSH, params);
                }
            }
        });
    }

    private void suspendAllAppsExceptAllowedApps(DevicePolicyManager dpm, ComponentName adminComponent) {
        PackageManager pm = getPackageManager(); // Get the PackageManager
        List<String> exemptPackages = Arrays.asList("com.android.settings", "com.example.vinstallment_server", "com.android.contacts", "com.android.phone");
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> packages = pm.queryIntentActivities(mainIntent, 0);
        List<ResolveInfo> MatchDefaultOnly = pm.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);

        for (ResolveInfo resolveInfo : packages) {
            try {
                String package_name = resolveInfo.activityInfo.packageName;
                if (!exemptPackages.contains(package_name)) {
                    String[] suspended = dpm.setPackagesSuspended(adminComponent, new String[]{package_name}, true);
                    if (suspended != null) {
//                        Log.i("AppSuspend", "Package suspended: " + suspended);
                    } else {
                        Log.i("AppSuspend", "Failed to suspend package: " + package_name);
                    }
                }
            } catch (Exception e) {
                Log.e("AppSuspend", "Error: " + e.getMessage());
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
