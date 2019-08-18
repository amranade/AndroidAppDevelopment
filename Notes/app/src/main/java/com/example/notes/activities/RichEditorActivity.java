package com.example.notes.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.notes.R;
import com.example.notes.common.CheckboxNoteRow;
import com.example.notes.common.INoteRowChangeListener;
import com.example.notes.common.NoteData;
import com.example.notes.common.NoteRow;
import com.example.notes.data.NotesDataProvider;
import com.example.notes.view.RichNoteEditorAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class RichEditorActivity extends AppCompatActivity {
    private static final String INTENT_PARAM_NOTE_ID = "note_id";
    public static final String NOTES_UPDATED = "notes_updated";
    public static final int NOTES_UPDATE_REQUEST_CODE = 3;

    private RecyclerView mNotesRowView;
    private RichNoteEditorAdapter mRichNoteEditorAdapter;
    private NotesDataProvider mNotesDataProvider;
    private INoteRowChangeListener mNoteRowChangeListener;
    private @Nullable Menu mOptionsMenu;

    private int mNoteId;
    boolean mIsDeleted;
    String mStartingText;
    NoteRow.RowType mCurrentNoteRowType;

    public static Intent launchIntentForNewNote(Context context) {
        return new Intent(context, RichEditorActivity.class);
    }

    public static Intent launchIntentForNoteId(Context context, int noteId) {
        Intent intent = new Intent(context, RichEditorActivity.class);
        intent.putExtra(INTENT_PARAM_NOTE_ID, noteId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mNoteId = intent.getIntExtra(INTENT_PARAM_NOTE_ID, -1);

        mNotesDataProvider = NotesDataProvider.getNotesDataProvider(this);
        mNoteRowChangeListener = createNoteRowChangeListener();

        NoteData noteData;
        if (mNoteId == -1) {
            // Start with a dummy note data before any save is complete
            noteData = new NoteData(mNoteId);
            CheckboxNoteRow row = new CheckboxNoteRow();
            noteData.setNoteRows(new ArrayList<NoteRow>(Arrays.asList(row)));
            mStartingText = "";
        } else {
            noteData = mNotesDataProvider.getNote(mNoteId);
            mStartingText = noteData.toText();
            // Assert that newNoteData.getNoteRows().size() == 0
            // Basically it's not possible to remove all rows after creating a note
            // Only possible way is to delete the note itself.
        }
        setContentView(R.layout.activity_rich_editor);
        mNotesRowView = findViewById(R.id.rich_notes_container);
        mRichNoteEditorAdapter = new RichNoteEditorAdapter(noteData, mNoteRowChangeListener);
        mRichNoteEditorAdapter.attachView(this, mNotesRowView);
        mCurrentNoteRowType = mRichNoteEditorAdapter.getCurrentFocusedRowtype();
        updateCurrentItemStyle(mCurrentNoteRowType);
    }

    private INoteRowChangeListener createNoteRowChangeListener() {
        return new INoteRowChangeListener() {
            @Override
            public void noteRowTypeFocused(NoteRow.RowType rowType) {
                if (mCurrentNoteRowType == rowType) return;
                mCurrentNoteRowType = rowType;
                updateCurrentItemStyle(rowType);
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        };
    }

    void setUpdateResult() {
        Intent data = new Intent();
        data.putExtra(NOTES_UPDATED, true);
        setResult(RESULT_OK, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rich_editor, menu);
        mOptionsMenu = menu;
        updateCurrentItemStyle(mCurrentNoteRowType);
        return true;
    }

    private Intent getShareIntent() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                mRichNoteEditorAdapter.getSharableText());
        return Intent.createChooser(sharingIntent, "Share via");
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
                return true;
            case R.id.style_paragraph_menu:
                updateCurrentItemStyle(NoteRow.RowType.PARAGRAPH);
                mRichNoteEditorAdapter.updateCurrentFocusedRowType(NoteRow.RowType.PARAGRAPH);
                return true;
            case R.id.style_checkbox_menu:
                updateCurrentItemStyle(NoteRow.RowType.CHECKBOX);
                mRichNoteEditorAdapter.updateCurrentFocusedRowType(NoteRow.RowType.CHECKBOX);
                return true;
            case R.id.action_share:
                startActivity(getShareIntent());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCurrentItemStyle(NoteRow.RowType rowType) {
        if (mOptionsMenu == null) return;
        MenuItem styleItem = mOptionsMenu.findItem(R.id.action_note_type_switch);
        switch (rowType) {
            case PARAGRAPH:
                mOptionsMenu.findItem(R.id.style_paragraph_menu).setChecked(true);
                styleItem.setTitle("TEXT");
                break;
            case CHECKBOX:
                mOptionsMenu.findItem(R.id.style_checkbox_menu).setChecked(true);
                styleItem.setTitle("CHECKBOX");
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsDeleted) {
            setUpdateResult();
            return;
        }
        String currentNoteText = mRichNoteEditorAdapter.getFullText();
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
        mNotesDataProvider.updateNote(mNoteId, mRichNoteEditorAdapter.getFullText());
    }

}
