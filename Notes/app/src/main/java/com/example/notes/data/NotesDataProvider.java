package com.example.notes.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.notes.common.NoteData;

import java.util.ArrayList;

public class NotesDataProvider {
    NotesSQLiteHelper mNotesSQLLiteHelper;
    @Nullable static NotesDataProvider mInstance;
    private NotesDataProvider(Context context) {
        // Dummy for now
        mNotesSQLLiteHelper = new NotesSQLiteHelper(context);
    }

    @NonNull
    public static NotesDataProvider getNotesDataProvider(Context context) {
        if (mInstance == null) {
            mInstance = new NotesDataProvider(context);
        }
        return mInstance;
    }

    public final ArrayList<NoteData> getAllNotes() {
        return mNotesSQLLiteHelper.getAllNotesBasic();
    }

    public final NoteData getNote(int noteId) {
        return  mNotesSQLLiteHelper.getFullNoteData(noteId);
    }

    public NoteData createNote() {
        return mNotesSQLLiteHelper.createNote();
    }

    public void updateNote(int noteId, String newText) {
        mNotesSQLLiteHelper.updateNote(noteId, newText);
    }

    public void deleteNote(int noteId) {
        mNotesSQLLiteHelper.deleteNote(noteId);
    }
}
