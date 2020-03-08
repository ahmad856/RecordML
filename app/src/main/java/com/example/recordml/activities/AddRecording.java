package com.example.recordml.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recordml.R;
import com.example.recordml.asynctasks.GetFileCategory;
import com.example.recordml.asynctasks.GetFileStatistics;
import com.example.recordml.constants.Constants;
import com.example.recordml.methods.Methods;
import com.example.recordml.models.Recording;
import com.example.recordml.models.Stats;
import com.example.recordml.speech.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.language.v1.model.ClassificationCategory;
import com.google.api.services.language.v1.model.Entity;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddRecording extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    static final String RECORD_KEY = "record";

    private Button record, add;
    private String date;
    private Stats stats;

    private StorageReference mStorage;

    List<ClassificationCategory> categoriesList;
    List<Entity> entitiesList;
    String categories = "";

    @SuppressLint("SimpleDateFormat")
    private final DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recording);

        initialize();
        setOnClickListeners();

        date = df.format(Calendar.getInstance().getTime());
    }

    private void initialize() {
        mStorage = Splash.getStorage().getReference();

        record = findViewById(R.id.record);
        add = findViewById(R.id.add);

        add.setEnabled(false);
    }

    private void setOnClickListeners() {
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddRecording.this, MainActivity.class), 456);
                record.setEnabled(false);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recording r = new Recording();
                r.setStamp(date);
                r.setTxtFilePath(Constants.PATH);
                r.setStats(stats);
                if (categoriesList != null) {
                    for (ClassificationCategory cc : categoriesList) {
                        categories = categories + cc.getName().replaceAll("/", "") + ",";
                    }
                }

                r.setCategories(categories);
                r.setTxtFileName(date + Constants.EXTENTION_TXT);

                File file = new File(Constants.PATH, date + "~" + categories + Constants.EXTENTION_TXT);
                try {
                    if (file.createNewFile()) {
                        Log.d("File", "file is created");
                    } else {
                        Log.d("File", "file is already present");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writeFile(file, stats.getFileContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri uri = Uri.fromFile(file);
                StorageReference textSumary = mStorage.child("TextSummarization/" + date + "~" + categories + Constants.EXTENTION_TXT);

                textSumary.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                exception.printStackTrace();
                            }
                        });
                Intent i = new Intent();
                i.putExtra(RECORD_KEY, r);
                AddRecording.this.setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    public static void writeFile(File file, String s) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, true);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write(s);
        bw.close();
    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 456 && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            String text = data.getExtras().getString(Constants.RECORDED_TEXT);

            if (!Methods.isNullOrEmpty(text)) {
                ((TextView) findViewById(R.id.textViewCheck)).setText(text);
                if (((TextView) findViewById(R.id.textViewCheck)).getText().toString().length() > 0)
                    add.setEnabled(true);
                new GetFileStatistics(this, null).execute(((TextView) findViewById(R.id.textViewCheck)).getText().toString());
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        record.setEnabled(true);
    }

    public void setStats(Stats stats) {
        this.stats = stats;
        //Async Task for file categories
        new GetFileCategory(this, stats.getFileContent()).execute(R.raw.credential);
    }

    public void setCategoryResponse(List<ClassificationCategory> categoriesList, List<Entity> entitiesList) {
        this.categoriesList = categoriesList;
        this.entitiesList = entitiesList;
    }
}
