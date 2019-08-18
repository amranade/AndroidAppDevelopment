package com.example.notes.common;

import java.util.ArrayList;

public class NoteData {

    private int mNoteId;
    private long mCreationTime;
    private long mUpdateTime;
    private ArrayList<NoteRow> mNoteRows;

    public NoteData(int noteId) {
        this.mNoteId = noteId;
    }

    public int getmNoteId() {
        return mNoteId;
    }

    public long getmCreationTime() {
        return mCreationTime;
    }

    public long getmUpdateTime() {
        return mUpdateTime;
    }

    public ArrayList<NoteRow> getNoteRows() {
        return mNoteRows;
    }

    public String getNoteBasicText() {
        StringBuilder builder = new StringBuilder();
        for (NoteRow row: mNoteRows) {
            builder.append(' ');
            builder.append(row.getBasicText());
        }
        return builder.toString().substring(1);
    }

    public String toText() {
        StringBuilder builder = new StringBuilder();
        for (NoteRow row: mNoteRows) {
            builder.append(row.toText());
        }
        return builder.toString();
    }

    public void setNoteRows(ArrayList<NoteRow> noteRows) {
        mNoteRows = noteRows;
    }

    public void addRow(NoteRow noteRow) {
        mNoteRows.add(noteRow);
    }

    public void setmNoteId(int noteId) {
        this.mNoteId = noteId;
    }

    public void setmCreationTime(long creationTime) {
        this.mCreationTime = creationTime;
    }

    public void setmUpdateTime(long updateTime) {
        this.mUpdateTime = updateTime;
    }

    public String getSharableText() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i< mNoteRows.size(); ++i) {
            if (i > 0) {
                builder.append('\n');
            }
            builder.append(mNoteRows.get(i).getSharableText());
        }
        return builder.toString();
    }
}
