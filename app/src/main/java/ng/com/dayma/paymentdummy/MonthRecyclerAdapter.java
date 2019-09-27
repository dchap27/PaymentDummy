package ng.com.dayma.paymentdummy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.MonthInfoEntry;

/**
 * Created by Ahmad on 7/31/2019.
 */

public class MonthRecyclerAdapter extends RecyclerView.Adapter<MonthRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mMonthIdPos;
    private int mIdPos;

    public MonthRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor == null)
            return;
        // Get column indexes from mCursor
        mMonthIdPos = mCursor.getColumnIndex(MonthInfoEntry.COLUMN_MONTH_ID);
        mIdPos = mCursor.getColumnIndex(MonthInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor){
        if(mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @Override
    public MonthRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_month_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String monthId = mCursor.getString(mMonthIdPos);
        int id = mCursor.getInt(mIdPos);
        DataManager dm = DataManager.getInstance();
        int totalSchedules = dm.getScheduleCount(monthId);
        holder.mTextMonth.setText(monthId);
        holder.mTextScheduleTotal.setText(mContext.getString(R.string.text_total_schedules_for_month) + String.valueOf(totalSchedules));
        holder.mId = id;
        holder.mMonthId = monthId;

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextMonth;
        public final TextView mTextScheduleTotal;
        private int mId;
        private String mMonthId;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextMonth = (TextView) itemView.findViewById(R.id.text_month_id_text);
            mTextScheduleTotal = (TextView) itemView.findViewById(R.id.text_schedule_total_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, ScheduleListActivity.class);
                    intent.putExtra(ScheduleListActivity.MONTH_ID, mMonthId);
                    mContext.startActivity(intent);
//                    Snackbar.make(v, mTextMonth.getText(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
