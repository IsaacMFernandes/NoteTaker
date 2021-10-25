package edu.floridapoly.mobiledeviceapps.fall21.notetaker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Tag to declare this object as a table in the database
@Entity(tableName = "note_table")
public class Note {

    // Tag to tell the database which variable is the primary key
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private int priority;


    // Standard methods and constructor
    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
