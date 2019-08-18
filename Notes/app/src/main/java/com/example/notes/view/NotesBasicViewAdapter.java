package com.example.notes.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.notes.R;
import com.example.notes.activities.RichEditorActivity;
import com.example.notes.common.NoteData;
import com.example.notes.data.NotesDataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NotesBasicViewAdapter
        extends RecyclerView.Adapter<NotesBasicViewAdapter.ViewHolder> {
    private @Nullable RecyclerView mNotesView;
    private @Nullable Activity mActivity;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<NoteData> mAllNotesData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup basicNoteContainer;
        public ViewHolder(ViewGroup container) {
            super(container);
            basicNoteContainer = container;
        }
    }

    private final NotesDataProvider mNotesDataProvider;

    public NotesBasicViewAdapter(Context context) {
        mNotesDataProvider = NotesDataProvider.getNotesDataProvider(context);
        refreshAllNotesData();
    }

    public void refreshAllNotesData() {
        mAllNotesData = mNotesDataProvider.getAllNotes();
    }

    public void attachToView(final Activity activity, @NonNull RecyclerView recyclerView) {
        mNotesView = recyclerView;
        mActivity = activity;
        final Context context = mNotesView.getContext();
        mLayoutManager = new LinearLayoutManager(context);
        mNotesView.setLayoutManager(mLayoutManager);
        mNotesView.setAdapter(this);
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        mNotesView.addItemDecoration(itemDecoration);
        mNotesView.setHasFixedSize(true);
        mNotesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int i = layoutManager.findFirstCompletelyVisibleItemPosition();
                Log.d("MyDebug", "position for first fully visible: " + i);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LinearLayout noteBasicContainer = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_basic_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(noteBasicContainer);
        return viewHolder;
    }

    private String getBasicString(String str) {
        int concatLength = 100;
        if (str.length() > concatLength) {
            str = str.substring(0, concatLength);
        }
        int newlineIndex = -1;
        int secondNewLine;
        newlineIndex = str.indexOf('\n');
        if (newlineIndex == -1) {
            return str;
        } else {
            secondNewLine = str.indexOf('\n', newlineIndex + 1);
            if (secondNewLine == -1) return str;
            if (secondNewLine == (newlineIndex + 1)) {
                return str.substring(0, newlineIndex);
            }
            return str.substring(0, secondNewLine);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        // Max concat length for now
        NoteData noteData = mAllNotesData.get(i);

        String basicText = getBasicString(noteData.getNoteBasicText());
        final int noteId = noteData.getmNoteId();
        final TextView noteTextView =
                viewHolder.basicNoteContainer.findViewById(R.id.basic_note_text);
        final TextView noteUpdateTimeView =
                viewHolder.basicNoteContainer.findViewById(R.id.basic_note_update_ts);
        viewHolder.basicNoteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity == null) return;
                mActivity.startActivityForResult(
                        RichEditorActivity.launchIntentForNoteId(mActivity, noteId),
                        RichEditorActivity.NOTES_UPDATE_REQUEST_CODE);
            }
        });
        noteTextView.setText(basicText);
        String timeStamp = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy")
                .format(noteData.getmUpdateTime());
        noteUpdateTimeView.setText(timeStamp);
    }


    @Override
    public int getItemCount() {
        return mNotesDataProvider.getAllNotes().size();
    }
}
