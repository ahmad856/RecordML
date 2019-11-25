package com.example.recordml.fragments;

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
import com.example.recordml.models.Recording;

public class RecordingsText extends Fragment {

    private Context context;
    private TextView text;
    private View contentView;
    private Recording record;

    public RecordingsText() {
    }

    public RecordingsText(Context context, Recording record) {
        this.context = context;
        this.record = record;
    }

    public static RecordingsText newInstance(String param1, String param2) {
        return new RecordingsText();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_recording_text, container, false);

        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView fileText = contentView.findViewById(R.id.fileText);

        fileText.setText(record.getStats().getFileContent());
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
