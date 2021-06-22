package com.example.mvvm.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mvvm.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import it.sephiroth.android.library.numberpicker.NumberPicker;

public class AddEditNote extends AppCompatActivity {

    public static final String EXTRA_ID =
            "com.example.mvvm.Activites.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.mvvm.Activites.EXTRA_TITLE";
    public static final String EXTRA_TEXT =
            "com.example.mvvm.Activites.EXTRA_TEXT";
    public static final String EXTRA_PRIORITY =
            "com.example.mvvm.Activites.EXTRA_PRIORITY";
    public static final String EXTRA_DATE=
            "com.example.mvvm.Activites.EXTRA_DATE";



    private NumberPicker numberPicker;
    private EditText note_text;
    private EditText note_title;
    private TextView header;
    private ImageView closeAddNote;
    private MaterialButton saveButton;
    private LinearLayout actionBar;
    private TextView date;
    private String currDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        numberPicker = findViewById(R.id.numberPicker);
        note_text = findViewById(R.id.note_text);
        note_title = findViewById(R.id.note_title);
        header = findViewById(R.id.addNote_Header);
        actionBar = findViewById(R.id.addNoteActionBar);
        saveButton = findViewById(R.id.save_note_button);
        closeAddNote = findViewById(R.id.close_add_note);
        date = findViewById(R.id.note_date);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        currDate = dateFormat.format(calendar.getTime());
        date.setText(currDate);

        Intent receiveIntent = getIntent();
        if(receiveIntent.hasExtra(EXTRA_ID)){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.blue));

            header.setText("Edit Note");
            actionBar.setBackgroundColor(getResources().getColor(R.color.blue));
            header.setBackgroundColor(getResources().getColor(R.color.blue));
            closeAddNote.setBackgroundColor(getResources().getColor(R.color.blue));
            saveButton.setBackgroundColor(getResources().getColor(R.color.purple_500));
            note_title.setText(receiveIntent.getStringExtra(EXTRA_TITLE));
            note_text.setText(receiveIntent.getStringExtra(EXTRA_TEXT));
            numberPicker.setProgress(receiveIntent.getIntExtra(EXTRA_PRIORITY,1));
        }else{
            header.setText("Add Note");
        }

        note_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.note_text) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }

        });



        closeAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void saveNote() {
        String title = note_title.getText().toString();
        String text = note_text.getText().toString();
        int priority = numberPicker.getProgress();
        

        if(title.trim().isEmpty() || text.trim().isEmpty()){
            Toasty.custom(this,"Please fill out the necessary details",R.drawable.ic_round_error_24,R.color.dark_blue,700,true,true).show();
            //Toasty.error(this, "Please fill out the necessary details", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE,title);
        intent.putExtra(EXTRA_TEXT,text);
        intent.putExtra(EXTRA_PRIORITY,priority);
        intent.putExtra(EXTRA_DATE,currDate);

        int note_id = getIntent().getIntExtra(EXTRA_ID,-1);
        if(note_id!=-1){
            intent.putExtra(EXTRA_ID,note_id);
        }

        setResult(RESULT_OK, intent);
        finish();

    }

}