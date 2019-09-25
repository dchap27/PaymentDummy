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

public class MonthRecyclerAdapter extends RecyclerView.Adapter<MonthRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private final List<MonthInfo> mMonths;
    private final LayoutInflater mLayoutInflater;
    private String mScheduleId;

    public MonthRecyclerAdapter(Context context, List<MonthInfo> months) {
        mContext = context;
        mMonths = months;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MonthRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_month_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MonthInfo month = mMonths.get(position);
        DataManager dm = DataManager.getInstance();
        int totalSchedules = dm.getScheduleCount(month.getMonthId());
        holder.mTextMonth.setText(month.getMonthId());
        holder.mTextScheduleTotal.setText(mContext.getString(R.string.text_total_schedules_for_month) + String.valueOf(totalSchedules));
        holder.mId = month.getId();
        holder.mMonth = month;

    }

    @Override
    public int getItemCount() {
        return mMonths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextMonth;
        public final TextView mTextScheduleTotal;
        private int mId;
        private MonthInfo mMonth;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextMonth = (TextView) itemView.findViewById(R.id.text_month_id_text);
            mTextScheduleTotal = (TextView) itemView.findViewById(R.id.text_schedule_total_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, ScheduleListActivity.class);
                    intent.putExtra(ScheduleListActivity.MONTH_ID, mId);
                    mContext.startActivity(intent);
//                    Snackbar.make(v, mTextMonth.getText(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
