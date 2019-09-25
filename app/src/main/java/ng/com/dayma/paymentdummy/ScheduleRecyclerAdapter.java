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

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private final List<ScheduleInfo> mSchedules;
    private final LayoutInflater mLayoutInflater;

    public ScheduleRecyclerAdapter(Context context, List<ScheduleInfo> schedules) {
        mContext = context;
        mSchedules = schedules;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ScheduleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_schedule_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ScheduleInfo schedule = mSchedules.get(position);
        DataManager dm = DataManager.getInstance();
        int payers = dm.getPaymentCount(schedule);
        List<PaymentInfo> payments = dm.getPayments(schedule);
        float totalAmount = dm.getScheduleAmount(payments);
        String status;
        if(schedule.isComplete()){
            status = "complete";
        } else {
            status = "draft";
        }
        holder.mScheduleTitle.setText(schedule.getScheduleId());
        holder.mTextMonth.setText(schedule.getMonth().getMonthId());
        holder.mTextTotalPayers.setText(mContext.getString(R.string.text_no_of_payers) + String.valueOf(payers));
        holder.mTextScheduleTotalAmount.setText(String.format(
                mContext.getString(R.string.text_total_amount_for_schedule), String.valueOf(totalAmount)));
        holder.mTextCompletionStatus.setText(String.format("Status: %s", status));
        holder.mId = schedule.getId();

    }

    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mScheduleTitle;
        public final TextView mTextMonth;
        public final TextView mTextTotalPayers;
        public final TextView mTextScheduleTotalAmount;
        private int mId;
        private final TextView mTextCompletionStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            mScheduleTitle = (TextView) itemView.findViewById(R.id.text_payer_title);
            mTextMonth = (TextView) itemView.findViewById(R.id.text_month_text);
            mTextTotalPayers = (TextView) itemView.findViewById(R.id.text_payers_number_text);
            mTextScheduleTotalAmount = (TextView) itemView.findViewById(R.id.text_amount_text);
            mTextCompletionStatus = (TextView) itemView.findViewById(R.id.text_status_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PaymentListActivity.class);
                    intent.putExtra(PaymentListActivity.SCHEDULE_ID, mId);
                    mContext.startActivity(intent);
//                    Snackbar.make(v, mScheduleTitle.getText(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
