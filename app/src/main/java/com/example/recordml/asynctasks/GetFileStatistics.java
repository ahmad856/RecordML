package com.example.recordml.asynctasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.recordml.activities.AddRecording;
import com.example.recordml.activities.RecordingsListView;
import com.example.recordml.models.Stats;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class GetFileStatistics extends AsyncTask<String, Void, Stats> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private int wordCount;
    private String fileName;
    private ProgressDialog progress;

    public GetFileStatistics(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    @Override
    protected Stats doInBackground(String... strings) {
        String current, longestWord, shortestWord, fileContent;
        Map<String, Integer> wordCountMap = new HashMap<>();
        fileContent = "";
        longestWord = "";
        shortestWord = "pneumonoultramicroscopicsilicovolcanoconiosis";

        try {
            Scanner sc = new Scanner(new File(strings[0]));

            while (sc.hasNext()) {
                current = sc.next();
                fileContent = fileContent + " " + current;
                wordCount++;
                if (current.length() > longestWord.length()) { longestWord = current; }
                if (current.length() < shortestWord.length()) { shortestWord = current; }
                if (wordCountMap.containsKey(current)) {
                    wordCountMap.put(current, wordCountMap.get(current) + 1);
                } else {
                    wordCountMap.put(current, 1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String mostRepeatedWord = "";
        int count = 0;
        Set<Map.Entry<String, Integer>> entrySet = wordCountMap.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getValue() > count) {
                mostRepeatedWord = entry.getKey();
                count = entry.getValue();
            }
        }

        String leastRepeatedWord = "";
        count = 999999999;
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getValue() < count) {
                leastRepeatedWord = entry.getKey();
                count = entry.getValue();
            }
        }

        Stats fileStats = new Stats();
        fileStats.setLeastOccurredWord(leastRepeatedWord);
        fileStats.setLongestWord(longestWord);
        fileStats.setMostOccurredWord(mostRepeatedWord);
        fileStats.setShortestWord(shortestWord);
        fileStats.setWordCount(wordCount);
        fileStats.setFileContent(fileContent);

        return fileStats;
    }

    @Override
    protected void onPostExecute(Stats stats) {
        super.onPostExecute(stats);
        if(context instanceof AddRecording) {
            ((AddRecording) context).setStats(stats);
        }
        else if(context instanceof RecordingsListView){
            ((RecordingsListView)context).setStats(stats, fileName);
        }
        progress.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progress.setMax(100);
        progress.setMessage("Working on Stats");

        progress.show();

    }
}
