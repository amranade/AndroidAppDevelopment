package com.example.notes.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.notes.R;
import com.example.notes.common.NoteData;
import com.example.notes.data.NotesDataProvider;


public class EditorActivity extends AppCompatActivity {

    private static String INTENT_PARAM_NOTE_ID = "note_id";
    private EditText mNoteText;
    private NotesDataProvider mNotesDataProvider;
    int mNoteId;
    String mStartingText;
    boolean mIsDeleted;

    public static final String NOTES_UPDATED = "notes_updated";
    public static final int NOTES_UPDATE_REQUEST_CODE = 2;

    public static Intent launchIntentForNewNote(Context context) {
        return new Intent(context, EditorActivity.class);
    }

    public static Intent launchIntentForNoteId(Context context, int noteId) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(INTENT_PARAM_NOTE_ID, noteId);
        return intent;
    }

    void setUpdateResult() {
        Intent data = new Intent();
        data.putExtra(NOTES_UPDATED, true);
        setResult(RESULT_OK, data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mNoteText = findViewById(R.id.note_text);
        mNotesDataProvider = NotesDataProvider.getNotesDataProvider(this);

        Intent intent = getIntent();
        mNoteId = intent.getIntExtra(INTENT_PARAM_NOTE_ID, -1);

        if (mNoteId == -1) {
            mStartingText = "";
        } else {
            NoteData noteData = mNotesDataProvider.getNote(mNoteId);
            mStartingText = noteData.getNoteBasicText();
        }
        mNoteText.setText(mStartingText);
        mNoteText.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (mNoteId != -1) {
                    mNotesDataProvider.deleteNote(mNoteId);
                }
                mIsDeleted = true;
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsDeleted) {
            setUpdateResult();
            return;
        }
        String currentNoteText = mNoteText.getText().toString();
        if (currentNoteText.equals(mStartingText)) {
            // For existing note, nothing new to be done.
            // For new note, nothing to be created.
            return;
        }
        if (mNoteId == -1) {
            // Make a new note first
            NoteData noteData = mNotesDataProvider.createNote();
            mNoteId = noteData.getmNoteId();
        }
        // A bit hack here for now for paragraph only
        currentNoteText = "<p>" + currentNoteText + "</p>";
        mNotesDataProvider.updateNote(mNoteId, currentNoteText);
    }
}
