package com.example.recordml.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.recordml.R;
import com.example.recordml.asynctasks.GetFileStatistics;
import com.example.recordml.models.Recording;
import com.example.recordml.models.Stats;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recording);

        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        record = findViewById(R.id.record);
        add = findViewById(R.id.add);

        //translate = findViewById(R.id.translate);
        stop.setEnabled(false);
        play.setEnabled(false);
        add.setEnabled(false);

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'~'HH:mm:ss");
        date = df.format(Calendar.getInstance().getTime());


        createFolder("TextSummarization");
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/TextSummarization/" ,date+".txt");
        try {
            if(file.createNewFile()){
                Log.d("File", "file is created");
            }else{
                Log.d("File", "file is already present");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFileAudio = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TextSummarization/"+date+".3gp";
        outputFileTxt = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TextSummarization/"+date+".txt";

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
                r.setTxtFile(outputFileTxt);
                r.setStats(stats);

                Intent i=new Intent();

                i.putExtra(RECORD_KEY, r);

                AddRecording.this.setResult(RESULT_OK,i);
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

    public static void writeFile(String file,String s) throws IOException {
        File f = new File(file);
        FileOutputStream fos = new FileOutputStream(f,true);
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
            ((TextView)findViewById(R.id.textViewCheck)).setText(((TextView)findViewById(R.id.textViewCheck)).getText().toString()+" "+result.get(0));
            try {
                writeFile(outputFileTxt, result.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }

            new GetFileStatistics(this).execute(outputFileTxt);
        }
    }

    private void translateToText() {
        voiceToText();

        record.setEnabled(false);
        stop.setEnabled(false);

//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setOnCompletionListener(AddRecording.this);
//        try {
//            mediaPlayer.setDataSource(outputFile);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        record.setEnabled(true);
    }

    public void setStats(Stats stats) {
        this.stats = stats;
        add.setEnabled(true);
    }
}
