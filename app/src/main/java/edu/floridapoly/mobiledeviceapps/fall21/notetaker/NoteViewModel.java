package edu.floridapoly.mobiledeviceapps.fall21.notetaker;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel
{
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);

        // Using our repository object, ex of encapsulation
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    // Methods that we've basically already implemented
    public void insert(Note note)
    {
        repository.insert(note);
    }

    public void update(Note note)
    {
        repository.update(note);
    }

    public void delete(Note note)
    {
        repository.delete(note);
    }

    public void deleteAllNotes()
    {
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes()
    {
        return allNotes;
    }
}
