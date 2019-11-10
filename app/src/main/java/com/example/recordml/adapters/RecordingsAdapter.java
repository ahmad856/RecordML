package com.example.recordml.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recordml.models.Recording;
import java.util.List;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder>  {
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
    public void onBindViewHolder(@NonNull RecordingViewHolder holder, int position) {
        if(items != null && items.size() > 0 && holder != null){
//            holder.date.setText(items.get(position).getDate());
//            holder.title.setText(items.get(position).getTitle());
//            holder.place.setText(items.get(position).getPlace());
        }
    }

    @Override
    public int getItemCount() {
        if(items!=null)
            return items.size();
        return 0;
    }

    class RecordingViewHolder extends RecyclerView.ViewHolder {

        public RecordingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
