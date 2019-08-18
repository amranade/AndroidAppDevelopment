package com.example.notes.common;

public class ParagraphNoteRow extends NoteRow implements ITextNoteRow {
    private String mParagraphText;

    public ParagraphNoteRow() {
        super(RowType.PARAGRAPH);
        mParagraphText = "";
    }

    protected static NoteRow fromRawText(String rawText) {
        ParagraphNoteRow paragraphNoteRow = new ParagraphNoteRow();
        paragraphNoteRow.mParagraphText = rawText;
        return paragraphNoteRow;
    }

    public String getParagraphText() {
        return mParagraphText;
    }

    @Override
    public String getBasicText() {
        return mParagraphText;
    }

    @Override
    public String getSharableText() {
        return mParagraphText;
    }

    @Override
    protected String getRowText() {
        return mParagraphText;
    }

    @Override
    public void setRowText(String txt) {
        mParagraphText = txt;
    }
}
