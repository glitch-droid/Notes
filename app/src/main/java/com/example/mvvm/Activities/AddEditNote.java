package com.example.mvvm.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
    private ImageView imageButton;
    private Uri filePath;
    private InputImage image;
    private ImageView imagePreview;

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
        imageButton = findViewById(R.id.selectImage_IV);
        imagePreview = findViewById(R.id.note_image);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
            imageButton.setBackgroundColor(getResources().getColor(R.color.blue));
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

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note_text.setText("");
                selectImage();

            }
        });
    }

    private void selectImage(){
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {
        super.onActivityResult(requestCode,
                resultCode,
                data);
        if (requestCode == 1
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            filePath = data.getData();
            try {
                image = InputImage.fromFilePath(this,filePath);
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
                getTextFromImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getTextFromImage(){
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result =
                recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Text text) {
                        String txt = text.getText();
                        if(txt.isEmpty()) {
                            Toasty.error(AddEditNote.this,"No Text Detected!!").show();
                        }else{
                            note_text.setText(txt);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });
    }

    private void saveNote() {
        String title = note_title.getText().toString();
        String text = note_text.getText().toString();
        int priority = numberPicker.getProgress();
        

        if(title.trim().isEmpty() || text.trim().isEmpty()){
            Toasty.info(this,"Please fill out the necessary details").show();
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