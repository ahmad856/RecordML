package com.example.recordml.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recordml.R;
import com.example.recordml.constants.Constants;
import com.example.recordml.models.Recording;

public class RecordingStats extends Fragment {

    private Context context;
    private Recording record;
    private TextView mostOccurredWord, wordCount, longestWord, shortestWord, leastOccurresWord;
    private View contentView;

    public RecordingStats() {
    }

    public RecordingStats(Context context, Recording record) {
        this.context = context;
        this.record = record;
    }

    public static RecordingStats newInstance() {
        return new RecordingStats();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_recording_stats, container, false);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        setStats();
    }

    private void initialize() {
        mostOccurredWord = contentView.findViewById(R.id.mostOccurredWord);
        wordCount = contentView.findViewById(R.id.wordCount);
        longestWord = contentView.findViewById(R.id.longestWord);
        shortestWord = contentView.findViewById(R.id.shortestWord);
        leastOccurresWord = contentView.findViewById(R.id.leastOccurredWord);
    }

    @SuppressLint("SetTextI18n")
    private void setStats() {
        mostOccurredWord.setText(record.getStats().getMostOccurredWord());
        wordCount.setText(Constants.EMPTY_STRING + record.getStats().getWordCount());
        longestWord.setText(record.getStats().getLongestWord());
        shortestWord.setText(record.getStats().getShortestWord());
        leastOccurresWord.setText(record.getStats().getLeastOccurredWord());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
