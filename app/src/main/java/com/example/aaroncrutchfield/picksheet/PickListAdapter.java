package com.example.aaroncrutchfield.picksheet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aaroncrutchfield.picksheet.data.PickListDbContract;
import com.example.aaroncrutchfield.picksheet.data.PickListDbUtility;

/**
 * Adapter for the RecyclerView containing the pick list entries
 */

class PickListAdapter extends RecyclerView.Adapter<PickListAdapter.EntryViewHolder>{
    private static final String TAG = PickListAdapter.class.getSimpleName();

    private final Context mContext;
    private final SQLiteDatabase mDb;
    private Cursor mCursorWithEntries;

    public PickListAdapter(Context context, SQLiteDatabase db) {
        mContext = context;
        mCursorWithEntries = PickListDbUtility.getPartnumbersForPickListAdapter(db);
        mDb = db;
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.pick_list_item, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        if (!mCursorWithEntries.moveToPosition(position)) return;

        String partnumber = mCursorWithEntries.getString(
                mCursorWithEntries.getColumnIndex(PickListDbContract.PickListEntry.COLUMN_PARTNUMBER));
        int total = 0;

        holder.tvPartnumber.setText(partnumber);
        holder.tvTotal.setText(String.valueOf(total));

        //set tag to partnumber to help with deleting ALL entries matching this partnumber
        holder.itemView.setTag(partnumber);

        Log.d(TAG, "onBindViewHolder returned:" + partnumber + ": " + total);
    }

    @Override
    public int getItemCount() {
        return mCursorWithEntries.getCount();
    }

    /**
     * Closes the current cursor, replaces it with a newCursor containing updated data and notifies
     * the RecyclerView it needs to refresh to display the updated data.
     * @param newCursor cursor containing the updated data
     */
    public void swapCursor(Cursor newCursor){
        //close the previous cursor
        if (mCursorWithEntries != null) mCursorWithEntries.close();
        mCursorWithEntries = newCursor;

        //force the recyclerView to refresh
        if (newCursor != null) this.notifyDataSetChanged();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPartnumber;
        public TextView tvTotal;

        public EntryViewHolder(View itemView) {
            super(itemView);
            tvPartnumber = (TextView) itemView.findViewById(R.id.tv_partnumber);
            tvTotal = (TextView) itemView.findViewById(R.id.tv_total);
        }
    }
}
