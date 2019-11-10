package com.example.recordml.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.recordml.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecordingsListView extends AppCompatActivity {
    FloatingActionButton addRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings_list_view);

        addRecording = findViewById(R.id.addRecording);

        addRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


}
