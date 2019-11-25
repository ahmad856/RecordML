package com.example.recordml.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.recordml.R;
import com.example.recordml.adapters.RecordingsAdapter;
import com.example.recordml.asynctasks.GetFileStatistics;
import com.example.recordml.constants.Constants;
import com.example.recordml.models.Recording;
import com.example.recordml.models.Stats;
import com.example.recordml.speech.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class RecordingsListView extends AppCompatActivity implements RecyclerView.OnItemTouchListener, OnCompleteListener<ListResult> {

    static final String RECORDING_KEY = "recording";
    private GestureDetector gestureDetector;
    FloatingActionButton addRecording;
    private List<Recording> items;
    RecordingsAdapter adapter;
    static RecyclerView rc;
    Task<ListResult> allFiles;
    public Map<String, Recording> downloadedFiles;
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

//                new GetAudioText(RecordingsListView.this, Constants.PATH+"test.3gp").execute(R.raw.credential);
//                startActivity(new Intent(RecordingsListView.this, MainActivity.class));
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

            if (file.exists()) {
                r.setDownloaded(true);
                downloadedFiles.put(child.getName(), r);
                String fileContent = getContentFromFile(file);
                new GetFileStatistics(RecordingsListView.this, childTmp.getName()).execute(fileContent);
            }
        }
    }

    private void addRecording(Recording r){
        if(r != null){
            items.add(r);
            Objects.requireNonNull(rc.getAdapter()).notifyDataSetChanged();
        }
    }

    public void setStats(Stats s, String fileName){
        Recording record = downloadedFiles.get(fileName);
        if(record != null)
            record.setStats(s);
        downloadedFiles.put(fileName,record);
        addRecording(record);
    }

    private String getContentFromFile(File f){
        String fileContent = "";
        try {
            Scanner sc = new Scanner(f);
            String current = "";
            while (sc.hasNext()) {
                current = sc.next();
                fileContent = fileContent + " " + current;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    public void setFileContent(String fileIndex, String s) {
        new GetFileStatistics(RecordingsListView.this, fileIndex).execute(s);
    }
}
