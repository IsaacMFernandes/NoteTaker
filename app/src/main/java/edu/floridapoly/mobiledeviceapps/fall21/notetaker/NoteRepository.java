package edu.floridapoly.mobiledeviceapps.fall21.notetaker;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository
{
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private ExecutorService service;

    public NoteRepository(Application application)
    {
        NoteDatabase database = NoteDatabase.getInstance(application);
        service = Executors.newSingleThreadExecutor();
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    // Using Async tasks for each method to be verbose, theres probably a better way to do this. Update: Found it!

    public void insert(Note note)
    {
        service.execute(() -> noteDao.insert(note));
    }

    public void update(Note note)
    {
        service.execute(() -> noteDao.update(note));
    }

    public void delete(Note note)
    {
        service.execute(() -> noteDao.delete(note));
    }

    public void deleteAllNotes()
    {
        service.execute(() -> noteDao.deleteAllNotes());
    }

    public LiveData<List<Note>> getAllNotes()
    {
        return allNotes;
    }
}
