package com.example.vinstallment_server;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import com.example.vinstallment_server.Receiver.InstallationResultReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FinalizedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalized);

        Button doneButton = findViewById(R.id.btn_done);
        doneButton.setOnClickListener(view -> {
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String fileName = "AppName.apk";
            destination += fileName;
            final Uri uri = Uri.parse("file://" + destination);

            File file = new File(destination);

            //get url of app on server
            String url = "https://dl3.pushbulletusercontent.com/Wqnk9msmMW0s1ocyaIUTTmcGkAkN1LUC/app-release.apk";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDestinationUri(uri);
            final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);

            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(uri,
                            manager.getMimeTypeForDownloadedFile(downloadId));
                    startActivity(install);

                    unregisterReceiver(this);
                    finish();
                }
            };

            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            try {
                InputStream in = new FileInputStream(file);
                PackageInfo apkInfo = getApplicationContext().getPackageManager().getPackageArchiveInfo(file.getPath(), 0);
                InstallPackage(getApplicationContext(), in, apkInfo.packageName);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Failed to install VInstallment " + e.getMessage(), Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }

            setResult(RESULT_OK);
            finish();
        });
    }
    private static void InstallPackage(Context applicationContext, InputStream in, String packageName) throws IOException {
        final PackageInstaller packageInstaller = applicationContext.getPackageManager().getPackageInstaller();
        final PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(packageName);

        final int sessionId = packageInstaller.createSession(params);
        final PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        final OutputStream out = session.openWrite("Vinstallment", 0, -1);
        final byte[] buffer = new byte[65536];
        int c;
        while ((c = in.read(buffer)) != -1) {
            out.write(buffer, 0, c);
        }
        session.fsync(out);
        in.close();
        out.close();

        session.commit(createIntentSender(applicationContext, sessionId));

    }

    private static IntentSender createIntentSender(Context applicationContext, int sessionId) {
        Intent intent = new Intent(applicationContext, InstallationResultReceiver.class); // Replace with your receiver class
        PendingIntent pendingIntent = PendingIntent.getBroadcast(applicationContext, sessionId, intent, PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent.getIntentSender();
    }

}