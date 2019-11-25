package com.example.recordml.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordml.R;
import com.example.recordml.models.Recording;

import java.util.List;

public class RecordingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Recording> items;
    private int itemLayout;

    public RecordingsAdapter(List<Recording> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new RecordingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (items != null && items.size() > 0) {
            if (holder != null) {
                RecordingViewHolder myViewHolder = (RecordingViewHolder) holder;
                myViewHolder.setFileName(items.get(position).getTxtFileName());
                myViewHolder.setCategory(items.get(position).getCategories());
                myViewHolder.setTimestamp(items.get(position).getStamp());
                myViewHolder.setFilePath(items.get(position).getTxtFilePath());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (items != null)
            return items.size();
        return 0;
    }

    class RecordingViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName, category, timestamp, filePath;

        RecordingViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            filePath = itemView.findViewById(R.id.path);
            category = itemView.findViewById(R.id.category);
            timestamp = itemView.findViewById(R.id.timeStamp);
        }

        void setFileName(String fileName) {
            this.fileName.setText(fileName);
        }

        void setCategory(String category) {
            this.category.setText(category);
        }

        void setFilePath(String filePath) {
            this.filePath.setText(filePath);
        }

        void setTimestamp(String timestamp) {
            this.timestamp.setText(timestamp);
        }
    }
}
