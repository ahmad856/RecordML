package com.example.recordml.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.Toast;
import com.example.recordml.R;
import com.example.recordml.adapters.RecordingsAdapter;
import com.example.recordml.asynctasks.GetFileStatistics;
import com.example.recordml.broascastrecievers.DownloadListenerImp;
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
public class RecordingsListView extends AppCompatActivity implements RecyclerView.OnItemTouchListener, OnCompleteListener<ListResult>, DownloadListenerImp {

    static final String RECORDING_KEY = "recording";
    private GestureDetector gestureDetector;
    FloatingActionButton addRecording;
    private List<Recording> items;
    RecordingsAdapter adapter;
    static RecyclerView rc;
    Task<ListResult> allFiles;
    public static Map<String, String> downloadedFiles;
    private File file;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            name = child.getName();

            file = new File(Constants.PATH, child.getName());

            if (!file.exists()) {
                downloadedFiles.put(name, Constants.NO);
                //download file
                child.getDownloadUrl().addOnSuccessListener(
                        new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Toast.makeText(RecordingsListView.this, "Download Success", Toast.LENGTH_SHORT).show();
                                downloadFile(RecordingsListView.this, name, Constants.FOLDER_NAME, uri.toString());
                                //add to recycler view
                                //addRecording(file.getPath(), name, name.replace(Constants.EXTENTION_TXT, Constants.EMPTY_STRING));
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecordingsListView.this, "Download Failure", Toast.LENGTH_SHORT).show();
                    }
                });


            } else {
                new GetFileStatistics(RecordingsListView.this).execute(file.getPath());
            }
        }
    }

//    public static void addRecording(String fileIndex){
//        if(downloadedFiles.get(fileIndex)!=null && !downloadedFiles.get(fileIndex).isEmpty() && Constants.YES.equalsIgnoreCase(downloadedFiles.get(fileIndex))){
//            //new GetFileStatistics(RecordingsListView.this).execute(file);
//        }
//    }

    private void addRecording(String filePath, String fileName, String timeStamp, Stats s){
        Recording r = new Recording();
        r.setTxtFilePath(filePath);
        r.setTxtFileName(fileName);
        r.setStamp(timeStamp);
        r.setStats(s);
        items.add(r);
        Objects.requireNonNull(rc.getAdapter()).notifyDataSetChanged();
    }

    public void setStats(Stats s){
        addRecording(file.getPath(), name, name.replace(Constants.EXTENTION_TXT, Constants.EMPTY_STRING), s);
    }

    @Override
    public void onDownloadComplete(String fileIndex) {
        if(downloadedFiles.get(fileIndex)!=null && !downloadedFiles.get(fileIndex).isEmpty() && Constants.YES.equalsIgnoreCase(downloadedFiles.get(fileIndex))){
            new GetFileStatistics(RecordingsListView.this).execute(file.getPath());
        }
    }
}
