package com.example.recordml.activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recordml.constants.Constants;
import com.example.recordml.models.Recording;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DownloadFiles extends AppCompatActivity implements OnCompleteListener<ListResult> {

    BroadcastReceiver download;
    public Map<String, Recording> downloadedFiles;
    Task<ListResult> allFiles;
    private File file;
    private String name;
    ProgressDialog progress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        downloadedFiles = new HashMap<>();
        progress = new ProgressDialog(DownloadFiles.this);
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progress.setMax(100);
        progress.setMessage("Setting up project please wait!!!");
        progress.show();

        StorageReference storageRef = Splash.getStorage().getReference();

        StorageReference textSumary = storageRef.child("TextSummarization/");
        allFiles = textSumary.listAll();
        allFiles.addOnCompleteListener(this);

        download = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    Log.d("Download", "Successfull");
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Cursor cursor = manager.query(query);
                    if (cursor.moveToFirst()) {
                        if (cursor.getCount() > 0) {
                            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                String file = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                                Log.d("Downloaded",file);
                                if(downloadedFiles.get(file)!=null && !downloadedFiles.get(file).isDownloaded()){
                                    downloadedFiles.get(file).setDownloaded(true);
                                    onDownloadComplete();
                                }
                            } else {
                                int message = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                                // So something here on failed.
                            }
                        }
                    }
                }
            }
        };

        registerReceiver(download, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private void downloadFile(Context context, String fileName, String destinationDirectory, String url) {

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //request.setDestinationInExternalFilesDir(this, destinationDirectory, fileName);
        request.setDestinationInExternalPublicDir(destinationDirectory, fileName);

        Objects.requireNonNull(downloadManager).enqueue(request);
    }

    @Override
    public void onComplete(@NonNull Task<ListResult> task) {
        for (StorageReference child : Objects.requireNonNull(task.getResult()).getItems()) {
            final StorageReference childTmp = child;

            String[] props = child.getName().split("~");

            name = props[0];
            String categories = "";
            if(props[1]!=null && !props[1].isEmpty()) {
                categories = props[1];
                categories = categories.replace(Constants.EXTENTION_TXT, Constants.EMPTY_STRING);
            }
//            String entities = "";
//            if(props[2]!=null && !props[2].isEmpty()){
//                entities = props[2];
//                entities = entities.replace(Constants.EXTENTION_TXT, Constants.EMPTY_STRING);
//            }

            file = new File(Constants.PATH, child.getName());
            Recording r = new Recording();
            r.setTxtFilePath(Constants.PATH);
            r.setTxtFileName(name + Constants.EXTENTION_TXT);
            r.setStamp(name);
            //r.setEntities(entities);
            r.setCategories(categories);

            if (!file.exists()) {
                downloadedFiles.put(child.getName(), r);
                //download file
                child.getDownloadUrl().addOnSuccessListener(
                        new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadFile(DownloadFiles.this, childTmp.getName(), Constants.FOLDER_NAME, uri.toString());
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DownloadFiles.this, "Download Failure", Toast.LENGTH_SHORT).show();
                    }
                });


            } else {
                r.setDownloaded(true);
                downloadedFiles.put(child.getName(), r);
//                String fileContent = getContentFromFile(file);
//                new GetFileStatistics(RecordingsListView.this, childTmp.getName()).execute(fileContent);
            }
        }
        onDownloadComplete();
    }

    public void onDownloadComplete() {
        //Recording record = downloadedFiles.get(fileIndex);
        //new GetFileContent(this, fileIndex).execute(fileIndex);
        //String fileContent = getContentFromFile(new File(fileIndex));
//        new GetFileStatistics(RecordingsListView.this, fileIndex).execute(fileContent);

        //check if all files have been downloaded
        boolean allDownloaded = true;
        Set<Map.Entry<String, Recording>> entrySet = downloadedFiles.entrySet();
        for (Map.Entry<String, Recording> entry : entrySet) {
            if (!entry.getValue().isDownloaded()) {
                allDownloaded = false;
            }
        }
        if(allDownloaded){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DownloadFiles.this, RecordingsListView.class);
                    startActivity(intent);
                    finish();
                    progress.hide();
                }
            }, 5000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(download);
    }
}
