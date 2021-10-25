package edu.floridapoly.mobiledeviceapps.fall21.notetaker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    // ViewModel is used to provide live data to the activity which simply updates the layout
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add a note button
        FloatingActionButton addNoteButton = (FloatingActionButton) findViewById(R.id.button_add_note);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the user clicks the add button, the add note activity is launched
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                addNoteActivityResultLauncher.launch(intent);
            }
        });

        // RecyclerView holds the notes
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Adapter connects the recycler view to each note item
        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        // Instantiating the view model and monitoring the notes
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        // Swipe to delete (left and right both work)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Method to swipe away notes
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnNoteClickListener((note -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
            intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
            intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
            intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
            editNoteActivityResultLauncher.launch(intent);
        }));
    }

    // Method to deal with data received from the add note activity
    ActivityResultLauncher<Intent> addNoteActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        // Get the data passed from the add note activity
                        Intent dataReceived = result.getData();
                        String title = dataReceived.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                        String description = dataReceived.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                        int priority = dataReceived.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

                        // The insert method takes a note object, so we create a note and insert
                        Note note = new Note(title, description, priority);
                        noteViewModel.insert(note);
                        Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Note not saved", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> editNoteActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent data = result.getData();
                        int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);

                        if (id == -1) {
                            Toast.makeText(MainActivity.this, "Note cannot be updated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                        String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                        int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

                        Note note = new Note(title, description, priority);
                        note.setId(id);
                        noteViewModel.update(note);

                        Toast.makeText(MainActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Note not updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Instantiating the menu that contains "delete all notes"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // Setting what happens when each menu option is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            // Only one menu option, delete all notes
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}