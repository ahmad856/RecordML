package com.example.recordml.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.example.recordml.R;
import com.example.recordml.adapters.RecordingsAdapter;
import com.example.recordml.asynctasks.GetFileStatistics;
import com.example.recordml.constants.Constants;
import com.example.recordml.models.Recording;
import com.example.recordml.models.Stats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class RecordingsListView extends AppCompatActivity implements RecyclerView.OnItemTouchListener, OnCompleteListener<ListResult> {

    static final String RECORDING_KEY = "recording";
    private GestureDetector gestureDetector;
    FloatingActionButton addRecording;
    private List<Recording> items;
    RecordingsAdapter adapter;
    static RecyclerView rc;
    Task<ListResult> allFiles;
    public static Map<String, Recording> downloadedFiles;
    private File file;
    private String name;
    BroadcastReceiver download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                                    onDownloadComplete(file);
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

        setContentView(R.layout.activity_recordings_list_view);
        items = new ArrayList<>();
        downloadedFiles = new HashMap<>();

        initialize();
        setOnClickListeners();

        StorageReference storageRef = Splash.getStorage().getReference();

        StorageReference textSumary = storageRef.child("TextSummarization/");
        allFiles = textSumary.listAll();
        allFiles.addOnCompleteListener(this);
    }

    private void initialize() {
        addRecording = findViewById(R.id.addRecording);
        adapter = new RecordingsAdapter(items, R.layout.item_rocord_veiw);
        rc = findViewById(R.id.recordsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(layoutManager);
        rc.addOnItemTouchListener(this);
        rc.setItemAnimator(new DefaultItemAnimator());
        rc.setAdapter(adapter);
    }

    private void setOnClickListeners() {
        addRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(RecordingsListView.this, AddRecording.class), 123);
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                View child = rc.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (child != null) {
                    //if tap was performed on some recyclerview row item
                    int i = rc.getChildAdapterPosition(child);    //index of item which was clicked
                    Recording r = items.get(i);
                    Intent intent = new Intent(RecordingsListView.this, RecordingsTab.class);
                    intent.putExtra(RECORDING_KEY, r);
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Recording r = (Recording) data.getExtras().getSerializable(AddRecording.RECORD_KEY);
            items.add(r);
            Objects.requireNonNull(rc.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

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
                                downloadFile(RecordingsListView.this, childTmp.getName(), Constants.FOLDER_NAME, uri.toString());
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecordingsListView.this, "Download Failure", Toast.LENGTH_SHORT).show();
                    }
                });


            } else {
                r.setDownloaded(true);
                downloadedFiles.put(name, r);

                new GetFileStatistics(RecordingsListView.this, name).execute(file.getPath());
            }
        }
    }

    private void addRecording(Recording r){
        items.add(r);
        Objects.requireNonNull(rc.getAdapter()).notifyDataSetChanged();
    }

    public void setStats(Stats s, String fileName){
        Recording record = downloadedFiles.get(fileName);
        record.setStats(s);
        downloadedFiles.put(fileName,record);
        addRecording(record);
    }

    public void onDownloadComplete(String fileIndex) {
        new GetFileStatistics(RecordingsListView.this, fileIndex).execute(file.getPath());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(download);
    }
}
