package com.esri.arcgisruntime.displaymapoffline;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    String[] appPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSION_REQUEST_CODE = 1240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (checkAndRequestPermissions()) {
            initApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();

            int deniedCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            if (deniedCount == 0) {
                initApp();
            }
        }
    }


    public boolean checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    private void initApp() {
        new MapDataAsynkTask().execute();
    }

    private boolean copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        boolean result = false;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
            result = false;
        }
        if (files != null) {
            String filename = MainActivity.OFFLINE_MAP_DATA;

            InputStream in = null;
            OutputStream out = null;
            try {

                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                result = true;
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
                result = false;
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        result = false;
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        result = false;
                    }
                }
            }
        }

        return result;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private class MapDataAsynkTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return copyAssets();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SplashActivity.this, "Map data copy failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
