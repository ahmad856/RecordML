package com.example.recordml.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordml.R;
import com.example.recordml.models.Recording;
import java.util.List;

public class RecordingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Recording> items;
    private int itemLayout;

    public RecordingsAdapter(List <Recording> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(itemLayout,parent,false);
        return new RecordingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(items != null && items.size() > 0 ){
            RecordingViewHolder myViewHolder = (RecordingViewHolder) holder;
            myViewHolder.setFileName(items.get(position).getTxtFile());
            //holder.setDescription().setText(items.get(position).get());
            myViewHolder.setTimestamp(items.get(position).getStamp());
        }
    }

    @Override
    public int getItemCount() {
        if(items!=null)
            return items.size();
        return 0;
    }

    class RecordingViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName, description, timestamp;

        public RecordingViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            description = itemView.findViewById(R.id.desc);
            timestamp = itemView.findViewById(R.id.timestamp);
        }

        public void setFileName(String fileName) { this.fileName.setText(fileName); }

        public void setDescription(String description) { this.description.setText(description); }

        public void setTimestamp(String timestamp) { this.timestamp.setText(timestamp); }
    }
}
