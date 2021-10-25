package edu.floridapoly.mobiledeviceapps.fall21.notetaker;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Setting the tables and version #
@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase
{
    private static NoteDatabase instance;

    // This method is implemented for us
    public abstract NoteDao noteDao();

    // Synchronized because this database may be used across multiple threads
    public static synchronized NoteDatabase getInstance(Context context)
    {
        // Database not made yet
        if (instance == null) {
            // Build database
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")

                    // Fallback
                    .fallbackToDestructiveMigration()

                    // Implemented below
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    // Using a call back and Async task to populate the database
    public static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
            /*ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    noteDao.insert(new Note("Title 1", "Description 1", 1));
                    noteDao.insert(new Note("Title 2", "Description 2", 2));
                    noteDao.insert(new Note("Title 3", "Description 3", 3));
                }
            });*/
        }
    };

    public static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private NoteDao noteDao;

        private PopulateDbAsyncTask(NoteDatabase db)
        {
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1", "Description 1", 1));
            noteDao.insert(new Note("Title 2", "Description 2", 2));
            noteDao.insert(new Note("Title 3", "Description 3", 3));
            return null;
        }
    }
}
