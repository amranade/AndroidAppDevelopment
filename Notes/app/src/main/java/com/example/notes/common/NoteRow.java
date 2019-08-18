package com.example.notes.common;

import java.util.ArrayList;

abstract public class NoteRow {
    public enum RowType {
        PARAGRAPH,
        CHECKBOX,
        BULLET,
        NUMERIC,
        HEADING,
        SUBHEADING;

        public static RowType fromInt(int i) {
            switch (i) {
                case 1:
                    return CHECKBOX;
                case 2:
                    return BULLET;
                case 3:
                    return NUMERIC;
                case 4:
                    return HEADING;
                case 5:
                    return SUBHEADING;
                case 0:
                default:
                    return PARAGRAPH;
            }
        }
    }

    // We'll store the string as html type tags
    // <tag>...</tag> is row structure after normalizing
    // tags are:
    // p is a paragraph
    // c is a checkbox
    // b is a bullet
    // n is a numeric
    // h is a heading
    // s is a subheading

    private static RowType fromTag(char tag) {
        switch (tag) {
            case 'c':
                return RowType.CHECKBOX;
            case 'b':
                return RowType.BULLET;
            case 'n':
                return RowType.NUMERIC;
            case 'h':
                return RowType.HEADING;
            case 's':
                return RowType.SUBHEADING;
            case 'p':
            default:
                return RowType.PARAGRAPH;
        }
    }

    private static char toTagText(RowType rowType) {
        switch (rowType) {
            case CHECKBOX:
                return 'c';
            case BULLET:
                return 'b';
            case NUMERIC:
                return 'n';
            case HEADING:
                return 'h';
            case SUBHEADING:
                return 's';
            case PARAGRAPH:
            default:
                return 'p';
        }
    }

    private static NoteRow makeRow(RowType rowType, String rowText) {
        switch (rowType) {
            case CHECKBOX:
                return CheckboxNoteRow.fromRawText(rowText);
            case PARAGRAPH:
            default:
                return ParagraphNoteRow.fromRawText(rowText);
            // add more later
        }
    }

    public static ArrayList<NoteRow> parseFromNoteRawText(String rawText) {
        ArrayList<NoteRow> rows = new ArrayList<>();
        int start_tag_idx, end_tag_idx, cur = 0, rawTextLen;
        String rowText;
        char tag;
        RowType rowType;
        while (cur < rawText.length()) {
            // get data
            start_tag_idx = cur;
            tag = rawText.charAt(start_tag_idx + 1);
            rowType = fromTag(tag);
            // The raw text can also contains more tags. So find closing tag for this tag
            end_tag_idx = rawText.indexOf("</" + tag + ">", cur + 1);
            rowText = rawText.substring(start_tag_idx + 3, end_tag_idx);
            // make row
            NoteRow row = makeRow(rowType, rowText);
            rows.add(row);
            // reset cur
            cur = end_tag_idx + 4;
        }
        return rows;
    }

    private final RowType mRowType;

    public NoteRow(RowType rowType) {
        mRowType = rowType;
    }

    public abstract String getBasicText();

    public abstract String getSharableText();

    public String toText() {
        char tag = toTagText(getRowType());
        return "<" + tag + ">" + getRowText() + "</" + tag + ">";
    }

    protected abstract String getRowText();

    public RowType getRowType() {
        return mRowType;
    }
}
