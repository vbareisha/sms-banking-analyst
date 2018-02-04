package com.bareisha.smsbankinganalyst.repository;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bareisha.smsbankinganalyst.R;
import com.bareisha.smsbankinganalyst.model.contract.SmsContract;
import com.bareisha.smsbankinganalyst.service.api.IItemOnClickHandler;

/**
 * Created by Vova on 28.01.2018.
 */

public class SmsCursorAdapter extends RecyclerView.Adapter<SmsCursorAdapter.SmsViewHolder> {

    private Context context;
    private Cursor cursor;
    private final IItemOnClickHandler onClickHandler;

    public SmsCursorAdapter(Context context, IItemOnClickHandler onClickHandler) {
        this.context = context;
        this.onClickHandler = onClickHandler;
    }

    @Override
    public SmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.sms_layout, parent, false);
        return new SmsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SmsViewHolder holder, int position) {
        int idIndex = cursor.getColumnIndex(SmsContract.SmsEntry._ID);
        int operationIndex = cursor.getColumnIndex(SmsContract.SmsEntry.COLUMN_OPERATION);

        cursor.moveToPosition(position);
        final int id = cursor.getInt(idIndex);
        String operation = cursor.getString(operationIndex);

        holder.itemView.setTag(id);
        holder.operation.setText(operation);
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (cursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = cursor;
        this.cursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    class SmsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView operation;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public SmsViewHolder(View itemView) {
            super(itemView);

            operation = itemView.findViewById(R.id.operation);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            long id = cursor.getLong(cursor.getColumnIndex(SmsContract.SmsEntry._ID));
            onClickHandler.onClick(id);
        }
    }

}
