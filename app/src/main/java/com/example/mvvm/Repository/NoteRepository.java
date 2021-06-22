package com.example.mvvm.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.mvvm.Model.Note;
import com.example.mvvm.DAO.NotesDAO;
import com.example.mvvm.Database.NotesDatabase;

import java.util.List;

public class NoteRepository {

    private NotesDAO notesDAO;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application){
        NotesDatabase database = NotesDatabase.getInstance(application);
        notesDAO = database.notesDAO();
        allNotes = notesDAO.getAllNotes();
    }


    public void insert(Note note ){
        new InsertAsyncTask(notesDAO).execute(note);
    }

    public void update(Note note){
        new UpdateAsyncTask(notesDAO).execute(note);
    }

    public void delete(Note note){
        new DeleteAsyncTask(notesDAO).execute(note);
    }

    public void deleteAllNotes(){
        new DeleteAllAsyncTask(notesDAO).execute();
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    private static class InsertAsyncTask extends AsyncTask<Note,Void, Void>{
        private NotesDAO notesDAO;

        private InsertAsyncTask(NotesDAO notesDAO) {
            this.notesDAO = notesDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            notesDAO.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Note,Void, Void>{
        private NotesDAO notesDAO;

        private UpdateAsyncTask(NotesDAO notesDAO) {
            this.notesDAO = notesDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            notesDAO.update(notes[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Note,Void, Void>{
        private NotesDAO notesDAO;

        private DeleteAsyncTask(NotesDAO notesDAO) {
            this.notesDAO = notesDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            notesDAO.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllAsyncTask extends AsyncTask<Void,Void, Void>{
        private NotesDAO notesDAO;

        private DeleteAllAsyncTask(NotesDAO notesDAO) {
            this.notesDAO = notesDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            notesDAO.deleteAll();
            return null;
        }
    }


}
