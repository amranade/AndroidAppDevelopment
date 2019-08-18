package com.example.notes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notes.common.NoteData;
import com.example.notes.common.NoteRow;

import java.util.ArrayList;

public class NotesSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notes_database";
    public static final String TABLE_NAME = "notes_data";
    public static final String NOTE_ID = "note_id";
    public static final String NOTE_CREATION_TIME = "creation_time";
    public static final String NOTE_UPDATE_TIME = "update_time";
    public static final String NOTE_TEXT = "note_text";

    public NotesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String creationQuery =
                "CREATE TABLE " + TABLE_NAME +
                "(" +
                NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTE_CREATION_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                NOTE_UPDATE_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                NOTE_TEXT + " XML" +
                ")";
        sqLiteDatabase.execSQL(creationQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public NoteData createNote() {
        SQLiteDatabase db = this.getWritableDatabase();
        long currentTimestamp = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(NOTE_TEXT, "");
        db.insert(TABLE_NAME, null, values);

        // Get note created, which should be last note id.
        db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{NOTE_ID},
                "",
                null,
                null,
                null,
                NOTE_ID + " DESC",
                "1");
        if (cursor != null)
            cursor.moveToFirst();
        return new NoteData(cursor.getInt(0));
    }

    public NoteData getFullNoteData(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{NOTE_ID, NOTE_UPDATE_TIME, NOTE_TEXT},
                NOTE_ID + "=?",
                new String[]{"" + noteId},
                null,
                null,
                null);
        if (cursor != null)
            cursor.moveToFirst();
        // Some error handling...
        int noteIdFetched = cursor.getInt(0);
        // assert noteIdFetched and noteId;
        long noteUpdateTime = cursor.getLong(1);
        String noteText = cursor.getString(2);
        NoteData noteData = new NoteData(noteIdFetched);
        noteData.setmUpdateTime(noteUpdateTime);
        noteData.setNoteRows(NoteRow.parseFromNoteRawText(noteText));
        return noteData;
    }

    public void updateNote(int noteId, String noteText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_UPDATE_TIME, System.currentTimeMillis());
        contentValues.put(NOTE_TEXT, noteText);
        db.update(TABLE_NAME, contentValues, NOTE_ID + "=?", new String[]{"" + noteId});
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, NOTE_ID + "=?",  new String[]{""+noteId});
    }

    public ArrayList<NoteData> getAllNotesBasic() {
        ArrayList<NoteData> allNotes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{NOTE_ID, NOTE_UPDATE_TIME, NOTE_TEXT},
                null,
                null,
                null,
                null,
                NOTE_UPDATE_TIME + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NoteData noteData = new NoteData(cursor.getInt(0));
            noteData.setmUpdateTime(cursor.getLong(1));
            noteData.setNoteRows(NoteRow.parseFromNoteRawText(cursor.getString(2)));
            allNotes.add(noteData);
            cursor.moveToNext();
        }
        return allNotes;
    }
}