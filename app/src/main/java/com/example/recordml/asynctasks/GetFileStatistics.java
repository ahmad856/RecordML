package com.example.recordml.asynctasks;

import android.os.AsyncTask;
import com.example.recordml.activities.AddRecording;
import com.example.recordml.models.Stats;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class GetFileStatistics extends AsyncTask<String,Void, Stats> {

    private AddRecording context;
    private Map<String, Integer> wordCountMap;
    private String current, longestWord, shortestWord, fileContent;
    private int wordCount;


    public GetFileStatistics(AddRecording context) { this.context = context; }

    @Override
    protected Stats doInBackground(String... strings) {
        wordCountMap = new HashMap<>();
        fileContent = "";
        longestWord = "";
        shortestWord = "pneumonoultramicroscopicsilicovolcanoconiosis";

        try {
            Scanner sc = new Scanner(new File(strings[0]));

            while (sc.hasNext()) {
                current = sc.next();
                fileContent = fileContent + " " + current;
                wordCount++;
                if (current.length() > longestWord.length()) {
                    longestWord = current;
                } if (current.length() < shortestWord.length()) {
                    shortestWord = current;
                } if(wordCountMap.containsKey(current)) {
                    wordCountMap.put(current, wordCountMap.get(current)+1);
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
        for (Map.Entry<String, Integer> entry : entrySet){
            if(entry.getValue() > count) {
                mostRepeatedWord = entry.getKey();
                count = entry.getValue();
            }
        }

        String leastRepeatedWord = "";
        count = 999999999;
        for (Map.Entry<String, Integer> entry : entrySet){
            if(entry.getValue() < count) {
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
    protected void onPostExecute(Stats stats){
        super.onPostExecute(stats);
        context.setStats(stats);
    }
}
