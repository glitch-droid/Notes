package com.example.mvvm.Database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mvvm.Model.Note;
import com.example.mvvm.DAO.NotesDAO;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Database(entities = {Note.class}, version = 1)
public abstract class NotesDatabase extends RoomDatabase {

    private static NotesDatabase instance;

    public abstract NotesDAO notesDAO();

    public static synchronized NotesDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NotesDatabase.class,"notes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new CallBackAsyncTask(instance).execute();
        }
    };

    private static class CallBackAsyncTask extends AsyncTask<Void,Void,Void>{
        private NotesDAO dao;

        public CallBackAsyncTask(NotesDatabase database) {
            dao = database.notesDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String currDate = dateFormat.format(calendar.getTime());

            dao.insert(new Note("placeholder","holder",-1,currDate));
            return null;
        }
    }
}
