package com.example.bears.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bears.R;
import com.example.bears.data.BearData;
import com.example.bears.network.BearDataListsner;
import com.example.bears.network.BearDataProvider;

public class BearsAdapter extends RecyclerView.Adapter<BearsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNameView;
        Button mUpdateButton;
        Button mDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.bear_name_view);
            mUpdateButton = itemView.findViewById(R.id.bear_name_update_button);
            mDeleteButton = itemView.findViewById(R.id.bear_delete_button);
        }
    }

    private class BearDataLisener implements BearDataListsner {

        @Override
        public void onBearsDataUpdated() {
            notifyDataSetChanged();
        }
    }

    private BearDataProvider mBearsDataProvider;
    private BearDataLisener mBearDataListener;
    private Context mContext;

    public BearsAdapter(Context context, BearDataProvider bearDataProvider) {
        mContext = context;
        mBearsDataProvider = bearDataProvider;
        mBearDataListener = new BearDataLisener();
        mBearsDataProvider.setmBearDataListsner(mBearDataListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.bear_view, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final BearData bear = mBearsDataProvider.getBear(i);
        viewHolder.mNameView.setText(bear.name);
        viewHolder.mDeleteButton.setOnClickListener(getDeleteBearListener(i, bear.id));
        viewHolder.mUpdateButton.setOnClickListener(getUpdateBearListener(i, bear.id));
    }

    private View.OnClickListener getUpdateBearListener(final int i, final String id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText bearName = new EditText(mContext);
                bearName.setText(mBearsDataProvider.getBear(i).name);
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Update name");
                alertDialog.setView(bearName);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBearsDataProvider.updateBearName(id, bearName.getText().toString());
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        };
    }

    private View.OnClickListener getDeleteBearListener(final int i, final String id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBearsDataProvider.deleteBear(id);
                // optimistic update here?
            }
        };
    }

    @Override
    public int getItemCount() {
        return mBearsDataProvider.getCount();
    }

}
