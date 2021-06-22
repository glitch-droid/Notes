package com.example.mvvm.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.Model.Note;
import com.example.mvvm.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends ListAdapter<Note,NotesAdapter.NotesHolder> {
    private OnItemClickListener listener;

    public NotesAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK =  new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Note oldItem, @NonNull @NotNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Note oldItem, @NonNull @NotNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getText().equals(newItem.getText()) &&
                    oldItem.getPriority() == newItem.getPriority();
        }
    };

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.notes_item_layout,parent,false);
        return new NotesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesHolder holder, int position) {
        Note noteObj = getItem(position);
        holder.priority.setText(String.valueOf(noteObj.getPriority()));
        holder.title.setText(noteObj.getTitle());
        holder.description.setText(noteObj.getText());
        holder.date.setText(noteObj.getDate());

    }


    public Note getNoteAt(int pos){
        return getItem(pos);
    }

    class NotesHolder extends RecyclerView.ViewHolder{
        private TextView priority;
        private TextView title;
        private TextView description;
        private TextView date;
        public NotesHolder(@NonNull View itemView) {
            super(itemView);
            priority = itemView.findViewById(R.id.text_priority);
            title = itemView.findViewById(R.id.text_title);
            description = itemView.findViewById(R.id.text_description);
            date = itemView.findViewById(R.id.text_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(listener!=null && pos!=RecyclerView.NO_POSITION){
                        listener.onItemClicked(getItem(pos));
                    }

                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClicked(Note note);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
