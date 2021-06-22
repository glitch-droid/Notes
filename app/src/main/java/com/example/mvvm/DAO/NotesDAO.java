package com.example.mvvm.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mvvm.Model.Note;

import java.util.List;

@Dao
public interface NotesDAO {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete()
    void delete(Note note);

    @Query("DELETE FROM notes_table")
    void deleteAll();

    @Query("SELECT * FROM notes_table ORDER BY priority DESC")
    LiveData<List<Note>> getAllNotes();
}
