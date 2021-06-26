package com.example.mvvm.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mvvm.Model.Note;
import com.example.mvvm.Adapter.NotesAdapter;
import com.example.mvvm.R;
import com.example.mvvm.ViewModel.NotesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_NOTE_REQUEST_CONST = 1;
    public static final int EDIT_NOTE_REQUEST_CONST = 2;

    private NotesViewModel notesViewModel;
    private TextView msgText1;
    private TextView msgText2;
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private FloatingActionButton fabAddNote;
    private FloatingActionButton deleteALLFAB;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animationView = findViewById(R.id.no_notes_anim);
        msgText1 = findViewById(R.id.messageText1);
        msgText2 = findViewById(R.id.messageText2);
        fabAddNote = findViewById(R.id.addNoteFAB);
        deleteALLFAB = findViewById(R.id.deleteAllFAB);

        recyclerView = findViewById(R.id.notes_RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        adapter = new NotesAdapter();
        recyclerView.setAdapter(adapter);

        notesViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NotesViewModel.class);
        notesViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if(notes.size()==0){
                    animationView.setVisibility(View.VISIBLE);
                    msgText1.setVisibility(View.VISIBLE);
                    msgText2.setVisibility(View.VISIBLE);
                }else{
                    animationView.setVisibility(View.GONE);
                    msgText1.setVisibility(View.GONE);
                    msgText2.setVisibility(View.GONE);
                }
                adapter.submitList(notes);
            }
        });



        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddEditNote.class);
                startActivityForResult(intent,ADD_NOTE_REQUEST_CONST);
            }
        });

        deleteALLFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesViewModel.deleteAllNotes();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                notesViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toasty.success(MainActivity.this,
                        "Deleted!").show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(note -> {
            Intent editIntent = new Intent(MainActivity.this, AddEditNote.class);
            editIntent.putExtra(AddEditNote.EXTRA_ID,note.getId());
            editIntent.putExtra(AddEditNote.EXTRA_TITLE,note.getTitle());
            editIntent.putExtra(AddEditNote.EXTRA_TEXT,note.getText());
            editIntent.putExtra(AddEditNote.EXTRA_PRIORITY,note.getPriority());
            editIntent.putExtra(AddEditNote.EXTRA_DATE,note.getDate());
            startActivityForResult(editIntent,EDIT_NOTE_REQUEST_CONST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_REQUEST_CONST && resultCode == RESULT_OK){
            assert data != null;
            String title = data.getStringExtra(AddEditNote.EXTRA_TITLE);
            String text = data.getStringExtra(AddEditNote.EXTRA_TEXT);
            int priority = data.getIntExtra(AddEditNote.EXTRA_PRIORITY,1);
            String currDate = data.getStringExtra(AddEditNote.EXTRA_DATE);

            Note newNote = new Note(title,text,priority,currDate);
            notesViewModel.inset(newNote);
            Toasty.success(this,
                    "Added!").show();
        }else if(requestCode == EDIT_NOTE_REQUEST_CONST && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditNote.EXTRA_ID,-1);
            if(id==-1){
                Toasty.custom(this,
                        "Error! Not Updated!",
                        R.drawable.ic_round_error_24,
                        R.color.dark_blue,
                        700,
                        true,
                        true).show();

            }else{
                Note note = new Note(
                        data.getStringExtra(AddEditNote.EXTRA_TITLE),
                        data.getStringExtra(AddEditNote.EXTRA_TEXT),
                        data.getIntExtra(AddEditNote.EXTRA_PRIORITY,0),
                        data.getStringExtra(AddEditNote.EXTRA_DATE)
                );
                note.setId(id);
                notesViewModel.update(note);
                Toasty.normal(this,
                        "Updated!").show();
            }

        }
    }
}