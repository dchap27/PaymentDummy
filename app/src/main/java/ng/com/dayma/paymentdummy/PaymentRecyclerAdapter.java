package ng.com.dayma.paymentdummy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.PaymentInfoEntry;

/**
 * Created by Ahmad on 7/31/2019.
 */

public class PaymentRecyclerAdapter extends RecyclerView.Adapter<PaymentRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private String mScheduleId;
    private int mChandaNoPos;
    private int mMemberNamePos;
    private int mLocalReceiptPos;
    private int mMonthPaidPos;
    private int mAmountPaidPos;
    private int mIdPos;

    public PaymentRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor == null)
            return;
        mChandaNoPos = mCursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_CHANDANO);
        mMemberNamePos = mCursor.getColumnIndex(PaymentInfoEntry.COLUMN_MEMBER_FULLNAME);
        mLocalReceiptPos = mCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_LOCALRECEIPT);
        mMonthPaidPos = mCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_MONTHPAID);
        mAmountPaidPos = mCursor.getColumnIndex(PaymentInfoEntry.COLUMN_PAYMENT_SUBTOTAL);
        mIdPos = mCursor.getColumnIndex(PaymentInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor){
        if(mCursor != null)
            mCursor.close();
        // create new cursor
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @Override
    public PaymentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_payment_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(PaymentRecyclerAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int chandaNo = mCursor.getInt(mChandaNoPos);
        String monthPaid = mCursor.getString(mMonthPaidPos);
        String receiptNo = mCursor.getString(mLocalReceiptPos);
        Float amountPaid = mCursor.getFloat(mAmountPaidPos);
        String fullname = mCursor.getString(mMemberNamePos);
        int id = mCursor.getInt(mIdPos);

        holder.mTextChandaNo.setText(String.format("%s: %s", String.valueOf(chandaNo), fullname));
        holder.mTextMonthPaid.setText(monthPaid);
        holder.mTextReceiptNo.setText(String.format(mContext.getString(R.string.text_receipt_number), receiptNo));
        holder.mTextAmountPaid.setText(String.format(mContext.getString(R.string.text_amount_paid), String.valueOf(amountPaid)));
        holder.mId = id;

    }

    @Override
    public int getItemCount() {
        return mCursor == null ?  0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTextChandaNo;
        public final TextView mTextMonthPaid;
        public final TextView mTextReceiptNo;
        public final TextView mTextAmountPaid;
        private int mId;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextChandaNo = (TextView) itemView.findViewById(R.id.text_payment_id);
            mTextMonthPaid = (TextView) itemView.findViewById(R.id.text_monthpaid_text);
            mTextReceiptNo = (TextView) itemView.findViewById(R.id.text_receipt_text);
            mTextAmountPaid = (TextView) itemView.findViewById(R.id.text_amountpaid_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PaymentActivity.class);
                    intent.putExtra(PaymentActivity.PAYMENT_ID, mId);
//                    intent.putExtra(PaymentActivity.SCHEDULE_INFO, mScheduleId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
