package ng.com.dayma.paymentdummy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.touchhelpers.ItemTouchHelperAdapter;
import ng.com.dayma.paymentdummy.touchhelpers.ScheduleClickAdapterListener;

/**
 * Created by Ahmad on 7/31/2019.
 */

public class MonthRecyclerAdapter extends RecyclerView.Adapter<MonthRecyclerAdapter.ViewHolder>
    implements ItemTouchHelperAdapter, View.OnTouchListener, GestureDetector.OnGestureListener{
    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mMonthIdPos;
    private int mIdPos;
    private ItemTouchHelper mTouchHelper;
    private ScheduleClickAdapterListener mListener;
    private ViewHolder mSelectedHolder;
    private GestureDetector mGestureDetector;

    public MonthRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
        mGestureDetector = new GestureDetector(mContext, this);
    }

    private void populateColumnPositions() {
        if(mCursor == null)
            return;
        // Get column indexes from mCursor
        mMonthIdPos = mCursor.getColumnIndex(PaymentProviderContract.Months.COLUMN_MONTH_ID);
        mIdPos = mCursor.getColumnIndex(PaymentProviderContract.Months._ID);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String monthId = mCursor.getString(mMonthIdPos);
        int id = mCursor.getInt(mIdPos);
        DataManager dm = DataManager.getInstance();
        int totalSchedules = dm.getScheduleCount(monthId);
        holder.mTextMonth.setText(monthId);
        holder.mTextScheduleTotal.setText(mContext.getString(R.string.text_total_schedules_for_month) + String.valueOf(totalSchedules));
        holder.mId = id;
        holder.mMonthId = monthId;

        ((ViewHolder)holder).parentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Action_Down", "Month List touch press detected");
                mSelectedHolder = holder;
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(mListener.onSingleClick()){
            mListener.onItemClicked(mSelectedHolder.getAdapterPosition(), mSelectedHolder.mId);
            return false;
        }
        Intent intent = new Intent(mContext, ScheduleListActivity.class);
        intent.putExtra(ScheduleListActivity.MONTH_ID, mSelectedHolder.mMonthId);
        mContext.startActivity(intent);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onItemSwiped(int position) {
        Log.d("ON SWIPE", "adapter position " + mSelectedHolder.getAdapterPosition() +
                " cursor id " + position);

    }
    public void setTouchHelper(ItemTouchHelper touchHelper){
        mTouchHelper = touchHelper;
    }

    public void setClickAdapter(ScheduleClickAdapterListener listener){
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextMonth;
        public final TextView mTextScheduleTotal;
        FrameLayout parentView;
        private int mId;
        private String mMonthId;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextMonth = (TextView) itemView.findViewById(R.id.text_month_id_text);
            mTextScheduleTotal = (TextView) itemView.findViewById(R.id.text_schedule_total_text);
            parentView = (FrameLayout) itemView.findViewById(R.id.frame_layout_month_list);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(mContext, ScheduleListActivity.class);
//                    intent.putExtra(ScheduleListActivity.MONTH_ID, mMonthId);
//                    mContext.startActivity(intent);
////                    Snackbar.make(v, mTextMonth.getText(), Snackbar.LENGTH_LONG).show();
//                }
//            });
        }
    }
}
