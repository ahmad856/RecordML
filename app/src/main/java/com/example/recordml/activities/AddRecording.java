package com.example.recordml.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recordml.R;
import com.example.recordml.asynctasks.GetFileCategory;
import com.example.recordml.asynctasks.GetFileStatistics;
import com.example.recordml.constants.Constants;
import com.example.recordml.models.Recording;
import com.example.recordml.models.Stats;
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

public class AddRecording extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    static final String RECORD_KEY = "record";

    private Button play, stop, record, add;
    //private Button translate;
    //private MediaRecorder myAudioRecorder;
    private String outputFileAudio, outputFileTxt;
    private String date;
    private Stats stats;
    //private MediaPlayer mediaPlayer;

    private StorageReference mStorage;

    /////////////////categories
    List<ClassificationCategory> categoriesList;
    List<Entity> entitiesList;
//    String entities = "";
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

        File file = new File(Constants.PATH, date + Constants.EXTENTION_TXT);
        try {
            if (file.createNewFile()) {
                Log.d("File", "file is created");
            } else {
                Log.d("File", "file is already present");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFileAudio = Constants.PATH + date + Constants.EXTENTION_3GP;
        outputFileTxt = Constants.PATH + date + Constants.EXTENTION_TXT;
    }

    private void initialize() {
        mStorage = Splash.getStorage().getReference();

        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        record = findViewById(R.id.record);
        add = findViewById(R.id.add);

        //translate = findViewById(R.id.translate);
        stop.setEnabled(false);
        play.setEnabled(false);
        //add.setEnabled(false);
    }

    private void setOnClickListeners() {
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceToText();
//                myAudioRecorder = new MediaRecorder();
//                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//                myAudioRecorder.setOutputFile(outputFile);
//                try {
//                    myAudioRecorder.prepare();
//                    myAudioRecorder.start();
//                } catch (IllegalStateException | IOException ise) {
//                    ise.printStackTrace();
//                }
                record.setEnabled(false);
                stop.setEnabled(true);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                myAudioRecorder.stop();
//                myAudioRecorder.release();
//                myAudioRecorder = null;
                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                //Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setEnabled(false);
                stop.setEnabled(false);

//                mediaPlayer = new MediaPlayer();
//                mediaPlayer.setOnCompletionListener(AddRecording.this);
//                try {
//                    mediaPlayer.setDataSource(outputFile);
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recording r = new Recording();
                r.setStamp(date);
                r.setTxtFilePath(outputFileTxt);
                r.setStats(stats);
                if(categoriesList!=null)for(ClassificationCategory cc : categoriesList){
                    categories = categories + cc.getName() + ",";
                }

                r.setCategories(categories);
//                r.setEntities(entities);
                //+"~"+entities
                r.setTxtFileName(date + "~"+categories + Constants.EXTENTION_TXT);

                Uri file = Uri.fromFile(new File(outputFileTxt));
                //+"~"+entities
                StorageReference textSumary = mStorage.child("TextSummarization/" + date + "~"+categories + Constants.EXTENTION_TXT);

                textSumary.putFile(file)
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

//        translate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                translateToText();
//            }
//        });
    }

    private void voiceToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");
        try {
            startActivityForResult(intent, 101);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void writeFile(String file, String s) throws IOException {
        File f = new File(file);
        FileOutputStream fos = new FileOutputStream(f, true);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write(s);
        bw.close();
    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            ////stop recording////
            //////////////////////
//            myAudioRecorder.stop();
//            myAudioRecorder.release();
//            myAudioRecorder = null;
            record.setEnabled(true);
            stop.setEnabled(false);
            play.setEnabled(true);
            //Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
            //////////////////////

            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ((TextView) findViewById(R.id.textViewCheck)).setText(((TextView) findViewById(R.id.textViewCheck)).getText().toString() + " " + result.get(0));
            try {
                writeFile(outputFileTxt, result.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Async Task for file statistics
            //+"~"+entities
            new GetFileStatistics(this, date + "~"+categories + Constants.EXTENTION_TXT).execute(outputFileTxt);
        }
    }

//    private void translateToText() {
//        voiceToText();
//
//        record.setEnabled(false);
//        stop.setEnabled(false);
//
////        mediaPlayer = new MediaPlayer();
////        mediaPlayer.setOnCompletionListener(AddRecording.this);
////        try {
////            mediaPlayer.setDataSource(outputFile);
////            mediaPlayer.prepare();
////            mediaPlayer.start();
////            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        record.setEnabled(true);
    }

    public void setStats(Stats stats) {
        this.stats = stats;
        //Async Task for file categories
        new GetFileCategory(this, stats.getFileContent()).execute(R.raw.credential);
        //add.setEnabled(true);
    }

    public void setCategoryResponse(List<ClassificationCategory> categoriesList, List<Entity> entitiesList){
        this.categoriesList = categoriesList;
        this.entitiesList = entitiesList;
        //add.setEnabled(true);
    }
}
