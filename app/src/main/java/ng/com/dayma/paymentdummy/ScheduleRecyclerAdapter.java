package ng.com.dayma.paymentdummy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.ScheduleInfoEntry;

/**
 * Created by Ahmad on 7/31/2019.
 */

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mScheduleIdPos;
    private int mMonthIdPos;
    private int mJamaatPos;
    private int mTitlePos;
    private int mStatusPos;
    private int mIdPos;
    private int mSubtotalPos;
    private int mInitialId = -1;
    private float mInitialSum = 0;
    private int mTotalPayersPos;

    public ScheduleRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPostions();
    }

    private void populateColumnPostions() {
        if(mCursor == null)
            return;
        mScheduleIdPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_ID);
        mMonthIdPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_MONTH_ID);
        mJamaatPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_JAMAAT);
        mTitlePos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_TITLE);
        mStatusPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_ISCOMPLETE);
        mSubtotalPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_TOTALAMOUNT);
        mTotalPayersPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_TOTALPAYERS);
        mIdPos = mCursor.getColumnIndex(ScheduleInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor){
        if(mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPostions();
        notifyDataSetChanged();
    }

    @Override
    public ScheduleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_schedule_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String scheduleId = mCursor.getString(mScheduleIdPos);
        String monthId = mCursor.getString(mMonthIdPos);
        String jamaat = mCursor.getString(mJamaatPos);
        String title = mCursor.getString(mTitlePos);
        int status = mCursor.getInt(mStatusPos);
        int id = mCursor.getInt(mIdPos);
        double subtotal = mCursor.getFloat(mSubtotalPos);
        int payers = mCursor.getInt(mTotalPayersPos);
        String completion;
        if(status == 0){
            completion = "Draft";
        } else {
            completion = "completed";
        }

        holder.mScheduleTitle.setText(title);
        holder.mTextMonth.setText(monthId);
        holder.mTextJamaatName.setText("Jamaat: " + jamaat);
        holder.mTextTotalPayers.setText(mContext.getString(R.string.text_no_of_payers) + String.valueOf(payers));
        holder.mTextScheduleTotalAmount.setText(String.format(
                mContext.getString(R.string.text_total_amount_for_schedule), String.valueOf(subtotal)));
        holder.mTextCompletionStatus.setText(String.format("Status: %s", completion));
        holder.mId = id;
        holder.mScheduleId = scheduleId;

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mScheduleTitle;
        public final TextView mTextMonth;
        public final TextView mTextTotalPayers;
        public final TextView mTextScheduleTotalAmount;
        public final TextView mTextJamaatName;
        public String mScheduleId;
        private int mId;
        private final TextView mTextCompletionStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            mScheduleTitle = (TextView) itemView.findViewById(R.id.text_payer_title);
            mTextMonth = (TextView) itemView.findViewById(R.id.text_month_text);
            mTextTotalPayers = (TextView) itemView.findViewById(R.id.text_payers_number_text);
            mTextScheduleTotalAmount = (TextView) itemView.findViewById(R.id.text_amount_text);
            mTextCompletionStatus = (TextView) itemView.findViewById(R.id.text_status_text);
            mTextJamaatName = (TextView) itemView.findViewById(R.id.text_jamaat_name_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PaymentListActivity.class);
                    intent.putExtra(PaymentListActivity.SCHEDULE_ID, mScheduleId);
                    mContext.startActivity(intent);
//                    Snackbar.make(v, mScheduleTitle.getText(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
