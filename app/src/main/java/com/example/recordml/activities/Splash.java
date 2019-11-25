package com.example.recordml.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.recordml.constants.Constants;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;

public class Splash extends AppCompatActivity {
    private static FirebaseStorage mStorage;

    public static FirebaseStorage getStorage() {
        if (mStorage == null) {
            mStorage = FirebaseStorage.getInstance();
        }
        return mStorage;
    }

    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void createFolder(String fname) {
        String myfolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fname;
        File f = new File(myfolder);
        if (!f.exists())
            if (!f.mkdir()) {
                Log.d("createFolder", myfolder + " can't be created.");
            } else
                Log.d("createFolder", myfolder + " can be created.");
        else
            Log.d("createFolder", myfolder + " already exits.");
    }

    @Override
    protected void onStart() {
        super.onStart();

        createFolder(Constants.FOLDER_NAME);

        //permissions
        if (!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, 333);
        else{
            Intent auth = new Intent(this, DownloadFiles.class);
            startActivity(auth);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 333) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(hasPermissions(this, PERMISSIONS)){
                    Intent auth = new Intent(this, DownloadFiles.class);
                    startActivity(auth);
                    finish();
                }
            } else {
                Toast.makeText(this, "All permissions should be allowed to use this app.", Toast.LENGTH_LONG).show();
                startInstalledAppDetailsActivity();
                finish();
            }
        }
    }

    private void startInstalledAppDetailsActivity() {
        Intent i = new Intent();
        i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
