package ng.com.dayma.paymentdummy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ahmad on 7/31/2019.
 */

public class PaymentRecyclerAdapter extends RecyclerView.Adapter<PaymentRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private final List<PaymentInfo> mPayments;
    private final LayoutInflater mLayoutInflater;
    private String mScheduleId;

    public PaymentRecyclerAdapter(Context context, List<PaymentInfo> payments) {
        mContext = context;
        mPayments = payments;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public PaymentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_payment_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(PaymentRecyclerAdapter.ViewHolder holder, int position) {

        PaymentInfo payment = mPayments.get(position);
        holder.mTextPaymentId.setText(String.format("%s: %s", String.valueOf(payment.getChandaNo()), payment.getFullname()));
        holder.mTextMonthPaid.setText(payment.getMonthPaid());
        holder.mTextReceiptNo.setText(String.format(mContext.getString(R.string.text_receipt_number), payment.getReceiptNo()));
        holder.mTextAmountPaid.setText(String.format(mContext.getString(R.string.text_amount_paid), String.valueOf(payment.getTotalAmountPaid())));
        holder.mId = payment.getId();
        ScheduleInfo schedule = payment.getSchedule();
        mScheduleId = schedule.getScheduleId();
    }

    @Override
    public int getItemCount() {
        return mPayments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTextPaymentId;
        public final TextView mTextMonthPaid;
        public final TextView mTextReceiptNo;
        public final TextView mTextAmountPaid;
        private int mId;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextPaymentId = (TextView) itemView.findViewById(R.id.text_payment_id);
            mTextMonthPaid = (TextView) itemView.findViewById(R.id.text_monthpaid_text);
            mTextReceiptNo = (TextView) itemView.findViewById(R.id.text_receipt_text);
            mTextAmountPaid = (TextView) itemView.findViewById(R.id.text_amountpaid_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PaymentActivity.class);
                    intent.putExtra(PaymentActivity.PAYMENT_ID, mId);
                    intent.putExtra(PaymentActivity.SCHEDULE_INFO, mScheduleId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
