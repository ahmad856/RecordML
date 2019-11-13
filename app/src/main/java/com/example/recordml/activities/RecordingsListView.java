package com.example.recordml.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.example.recordml.R;
import com.example.recordml.adapters.RecordingsAdapter;
import com.example.recordml.models.Recording;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class RecordingsListView extends AppCompatActivity implements RecyclerView.OnItemTouchListener{

    static final String RECORDING_KEY = "recording";
    private GestureDetector gestureDetector;
    FloatingActionButton addRecording;
    private List<Recording> items;
    RecordingsAdapter adapter;
    RecyclerView rc;

    // Requesting permission to RECORD_AUDIO
    private String [] PERMISSIONS = { Manifest.permission.RECORD_AUDIO,
                                      Manifest.permission.READ_EXTERNAL_STORAGE,
                                      Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 333) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
        setContentView(R.layout.activity_recordings_list_view);
        items = new ArrayList<>();
        addRecording = findViewById(R.id.addRecording);

        addRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(RecordingsListView.this, AddRecording.class),123);
            }
        });

        adapter = new RecordingsAdapter(items, R.layout.item_rocord_veiw);

        rc = findViewById(R.id.recordsRecyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        rc.setLayoutManager(layoutManager);
        rc.addOnItemTouchListener(this);
        rc.setItemAnimator(new DefaultItemAnimator());
        rc.setAdapter(adapter);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) { return false; }

            @Override
            public void onShowPress(MotionEvent motionEvent) { }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                View child = rc.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if(child != null){
                    //if tap was performed on some recyclerview row item
                    int i = rc.getChildAdapterPosition(child);	//index of item which was clicked
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
        //permissions
        if(!hasPermissions(this, PERMISSIONS))ActivityCompat.requestPermissions(this, PERMISSIONS, 333);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==123 && resultCode==RESULT_OK && data!=null && data.getExtras()!=null){
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
}
