package com.example.notes.common;

public class CheckboxNoteRow extends NoteRow implements ITextNoteRow {

    boolean mIsChecked;
    String mCheckboxText;
    public CheckboxNoteRow() {
        super(RowType.CHECKBOX);
        mIsChecked = false;
        mCheckboxText = "";
    }

    protected static NoteRow fromRawText(String rawText) {
        CheckboxNoteRow checkboxNoteRow = new CheckboxNoteRow();
        // The format is 0||1:<text>
        checkboxNoteRow.mIsChecked = rawText.charAt(0) == '1';
        checkboxNoteRow.mCheckboxText = rawText.substring(2);
        return checkboxNoteRow;
    }

    @Override
    public String getBasicText() {
        return mCheckboxText;
    }

    @Override
    public String getSharableText() {
        return "[" + (mIsChecked ? 'X' : ' ') + "] " + mCheckboxText;
    }

    @Override
    public String getRowText() {
        return "" + (mIsChecked ? '1' : '0') + ":" + mCheckboxText;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public String getCheckboxText() {
        return mCheckboxText;
    }

    @Override
    public void setRowText(String txt) {
        mCheckboxText = txt;
    }

    public void setIsChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }
}
