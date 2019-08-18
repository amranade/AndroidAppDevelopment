package com.example.notes.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.notes.R;
import com.example.notes.common.CheckboxNoteRow;
import com.example.notes.common.INoteRowChangeListener;
import com.example.notes.common.ITextNoteRow;
import com.example.notes.common.NoteData;
import com.example.notes.common.NoteRow;
import com.example.notes.common.ParagraphNoteRow;

import java.util.ArrayList;

public class RichNoteEditorAdapter extends RecyclerView.Adapter<RichNoteEditorAdapter.ViewHolder> {
    // Note Rows are stored per row.
    // Type used will be NoteRow.RowType

    private static final int BOTTOM_PADDING_VIEW_TYPE = -1;

    private NoteData mNoteData;
    private @Nullable RecyclerView mNoteRowsView;
    private @Nullable Activity mActivity;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<TextWatcher> mTextNoteRowWatchers;
    private final INoteRowChangeListener mNoteRowChangeListener;
    private int mCurrentFocusedRowPosition;

    public RichNoteEditorAdapter(NoteData noteData, INoteRowChangeListener noteRowChangeListener) {
        mNoteData = noteData;
        mTextNoteRowWatchers = new ArrayList<>();
        mNoteRowChangeListener = noteRowChangeListener;
        mCurrentFocusedRowPosition = 0;
    }

    public String getFullText() {
        return mNoteData.toText();
    }

    public String getSharableText() {
        return mNoteData.getSharableText();
    }

    public void attachView(final Activity activity, RecyclerView noteRowsView) {
        mActivity = activity;
        mNoteRowsView = noteRowsView;
        noteRowsView.setAdapter(this);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mNoteRowsView.setLayoutManager(mLayoutManager);
        mNoteRowsView.post(new Runnable() {
            @Override
            public void run() {
                // Set focus on the first row
                ViewHolder firstRow = (ViewHolder) mNoteRowsView.findViewHolderForAdapterPosition(0);
                firstRow.mNoteRowTextView.requestFocus();
            }
        });
    }

    public void updateCurrentFocusedRowType(NoteRow.RowType rowType) {
        if (getCurrentFocusedRowtype() == rowType) {
            return;
        }
        // Just use the text from the existing row to make new RowType
        String existingText = mNoteData.getNoteRows().get(mCurrentFocusedRowPosition).getBasicText();
        ITextNoteRow newNoteRow = null;
        switch (rowType) {
            case PARAGRAPH:
                newNoteRow = new ParagraphNoteRow();
                break;
            case CHECKBOX:
                newNoteRow = new CheckboxNoteRow();
        }
        newNoteRow.setRowText(existingText);
        ArrayList<NoteRow> newNoteRows = new ArrayList<>();
        for (int i=0;i<mNoteData.getNoteRows().size();++i) {
            if (i == mCurrentFocusedRowPosition) {
                newNoteRows.add((NoteRow) newNoteRow);
            } else {
                newNoteRows.add(mNoteData.getNoteRows().get(i));
            }
        }
        mNoteData.setNoteRows(newNoteRows);
        updateItemsWithFocus(mCurrentFocusedRowPosition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BOTTOM_PADDING_VIEW_TYPE) {
            FrameLayout dummyPadding = (FrameLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_row_bottom, parent, false);
            return new ViewHolder(dummyPadding);
        }
        NoteRow.RowType rowType = NoteRow.RowType.fromInt(viewType);
        switch (rowType) {
            case CHECKBOX:
                LinearLayout checkboxView = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_row_checkbox, parent, false);
                EditText checkBoxTextView = checkboxView.findViewById(R.id.checkbox_text);
                return new ViewHolder(checkboxView, rowType)
                        .setNoteRowTextView(checkBoxTextView);
            case PARAGRAPH:
            default:
                EditText paragraphView = (EditText) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_row_paragraph, parent, false);
                return new ViewHolder(paragraphView, rowType)
                        .setNoteRowTextView(paragraphView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if (position == mNoteData.getNoteRows().size()) return;
        EditText noteRowTextView = null;
        final NoteRow.RowType rowType = viewHolder.mRowType;
        switch (rowType) {
            case PARAGRAPH:
                noteRowTextView = (EditText) viewHolder.mNoteRowView;
                ParagraphNoteRow paragraphNoteRow =
                        (ParagraphNoteRow) mNoteData.getNoteRows().get(position);
                noteRowTextView.setText(paragraphNoteRow.getParagraphText());
                break;
            case CHECKBOX:
                LinearLayout checkboxView = (LinearLayout) viewHolder.mNoteRowView;
                CheckboxNoteRow checkboxNoteRow =
                        (CheckboxNoteRow) mNoteData.getNoteRows().get(position);
                AppCompatCheckBox checkBox = checkboxView.findViewById(R.id.checkbox_check_mark);
                noteRowTextView = checkboxView.findViewById(R.id.checkbox_text);
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(checkboxNoteRow.isChecked());
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CheckboxNoteRow checkBoxRow =
                                (CheckboxNoteRow) mNoteData.getNoteRows().get(position);
                        checkBoxRow.setIsChecked(isChecked);
                    }
                });
                noteRowTextView.setText(checkboxNoteRow.getCheckboxText());
                break;
        }
        if ((mNoteData.getNoteRows().get(position) instanceof ITextNoteRow) && (noteRowTextView != null)) {
            if (position == mTextNoteRowWatchers.size()) {
                mTextNoteRowWatchers.add(createTextNoteRowWatcher(position));
            }
            final EditText textView = noteRowTextView;
            textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    final EditText editText = (EditText) v;
                    if (hasFocus) {
                        updateCurrentFocusedRow(position);
                        editText.addTextChangedListener(mTextNoteRowWatchers.get(position));
                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                    } else {
                        editText.removeTextChangedListener(mTextNoteRowWatchers.get(position));
                    }
                }
            });
            textView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    EditText editText = (EditText) v;
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        // remove the listener so that repeat events will not happen.
                        textView.setOnKeyListener(null);
                        deleteNoteRow(position, editText.getText().toString());
                    }
                    return false;
                }
            });
        }
    }

    public NoteRow.RowType getCurrentFocusedRowtype() {
        return mNoteData.getNoteRows().get(mCurrentFocusedRowPosition).getRowType();
    }

    private void updateCurrentFocusedRow(int position) {
        mCurrentFocusedRowPosition = position;
        mNoteRowChangeListener.noteRowTypeFocused(getCurrentFocusedRowtype());
    }

    private void deleteNoteRow(int position, String appendedStr) {
        if (position == 0) return;
        int cursorInPrevString = -1;
        ArrayList<NoteRow> newRows = new ArrayList<>();
        for (int i=0;i< mNoteData.getNoteRows().size(); ++i) {
            if (i == (position - 1)) {
                NoteRow noteRow = mNoteData.getNoteRows().get(i);
                if (noteRow instanceof ITextNoteRow) {
                    if (appendedStr.length() > 0) {
                        cursorInPrevString = noteRow.getBasicText().length();
                    }
                    ((ITextNoteRow) noteRow).setRowText(
                            noteRow.getBasicText() + appendedStr);
                }
            }
            if (i == position) continue;
            newRows.add(mNoteData.getNoteRows().get(i));
        }
        mNoteData.setNoteRows(newRows);
        updateItemsWithFocus(position - 1, cursorInPrevString);
    }

    private void updateItemsWithFocus(final int focusPosition) {
        updateItemsWithFocus(focusPosition, -1);
    }

    private void updateItemsWithFocus(final int focusPosition, final int atCursor) {
        mNoteRowsView.setPreserveFocusAfterLayout(true);
        notifyDataSetChanged();
        mNoteRowsView.requestLayout();
        mNoteRowsView.post(new Runnable() {
            @Override
            public void run() {
                ViewHolder nextPara =
                        (ViewHolder) mNoteRowsView.findViewHolderForAdapterPosition(focusPosition);
                if (nextPara == null) return;
                EditText textView = nextPara.mNoteRowTextView;
                textView.requestFocus();
                if (atCursor == -1) {
                    textView.setSelection(textView.getText().length());
                } else {
                    textView.setSelection(atCursor);
                }
            }
        });
    }

    private TextWatcher createTextNoteRowWatcher(final int position) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /*
                \n => 2 splits of "", ""
                \ntxt => "" + txt
                txt\n => txt + ""
                 */

                Log.d("MyDebug", "text changed in editor at: " + position);
                // For now just assume regular typing.
                // If the \n gets added, we need to start a new paragraph
                NoteRow noteRow = mNoteData.getNoteRows().get(position);
                ((ITextNoteRow) noteRow).setRowText(s.toString());
                if (s.length() == 0) return;
                final String str = s.toString();
                int splitIndex = str.indexOf('\n');
                if (splitIndex == -1) return;
                ArrayList<String> allSplits = new ArrayList<>();
                if (str.length() == 1) {
                    allSplits.add("");
                }
                String[] allRowsText = str.split("\n");
                for (String txt : allRowsText) {
                    allSplits.add(txt);
                }
                if (str.charAt(str.length()-1) == '\n') {
                    allSplits.add("");
                }
//                s.delete(splitIndex + 1, s.length());
//                ((ITextNoteRow) noteRow).setRowText(str.substring(0, splitIndex));
                switch (noteRow.getRowType()) {
                    case PARAGRAPH:
                    case CHECKBOX:
                        createNewRowOnNewLine(position, allSplits, noteRow.getRowType());
                        break;
                }
            }
        };
    }

    private void createNewRowOnNewLine(int position, ArrayList<String> replaceTexts, NoteRow.RowType rowType) {
        ArrayList<NoteRow> newRows = new ArrayList<>();
        int focusIndex = position;
        for (int i = 0; i < mNoteData.getNoteRows().size(); ++i) {
//            newRows.add(mNoteData.getNoteRows().get(i));
            if (i == position) {
                for (String txt : replaceTexts) {
                    ITextNoteRow row;
                    switch (rowType) {
                        case CHECKBOX:
                            row = new CheckboxNoteRow();
                            if (focusIndex == position) {
                                ((CheckboxNoteRow) row).setIsChecked(
                                        ((CheckboxNoteRow) mNoteData.getNoteRows().get(i)).isChecked());
                            }
                            break;
                        case PARAGRAPH:
                        default:
                            row = new ParagraphNoteRow();
                            break;
                    }
                    row.setRowText(txt);
                    newRows.add((NoteRow) row);
                    focusIndex++;
                }
            } else {
                newRows.add(mNoteData.getNoteRows().get(i));
            }
        }
        mNoteData.setNoteRows(newRows);
        updateItemsWithFocus(focusIndex - 1, 0);
    }

    @Override
    public int getItemCount() {
        return mNoteData.getNoteRows().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mNoteData.getNoteRows().size()) return BOTTOM_PADDING_VIEW_TYPE;
        return mNoteData.getNoteRows().get(position).getRowType().ordinal();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mNoteRowView;
        public EditText mNoteRowTextView;
        public NoteRow.RowType mRowType;
        public ViewHolder(View itemView) {
            // To be used for bottom padding
            super(itemView);
            mNoteRowView = itemView;
        }

        public ViewHolder(View itemView, NoteRow.RowType rowType) {
            super(itemView);
            mNoteRowView = itemView;
            mRowType = rowType;
        }
        public ViewHolder setNoteRowTextView(EditText noteRowTextView) {
            mNoteRowTextView = noteRowTextView;
            return this;
        }
    }
}
