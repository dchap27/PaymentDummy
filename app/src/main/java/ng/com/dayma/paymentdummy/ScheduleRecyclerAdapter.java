package ng.com.dayma.paymentdummy;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.touchhelpers.ScheduleClickAdapterListener;

/**
 * Created by Ahmad on 7/31/2019.
 */

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>
        implements View.OnTouchListener,GestureDetector.OnGestureListener{
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
    private int mTotalPayersPos;
    public final String TAG = getClass().getSimpleName();
    private GestureDetector mGestureDetector;
    private ViewHolder mSelectedHolder;
    private ScheduleClickAdapterListener mListener;
    //
    private SparseBooleanArray mSelectedItemsPosition;
    private SparseBooleanArray mSelectedItemsCursorId;
    private int currentSelectedIndex = -1;

    public ScheduleRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPostions();
        mGestureDetector = new GestureDetector(mContext, this);
        mSelectedItemsPosition = new SparseBooleanArray();
        mSelectedItemsCursorId = new SparseBooleanArray();

    }

    public void toggleSelection(int position, int idPos){
        currentSelectedIndex = position;
        if(mSelectedItemsPosition.get(position, false)){
            mSelectedItemsPosition.delete(position);
            mSelectedItemsCursorId.delete(idPos);
        } else {
            mSelectedItemsPosition.put(position, true);
            mSelectedItemsCursorId.put(idPos, true);
        }
        notifyItemChanged(position);

    }
    public void selectAll(){
        for (int i = 0; i < getItemCount(); i++)
            mSelectedItemsPosition.put(i, true);
        notifyDataSetChanged();
    }

    public void clearSelections(){
        mSelectedItemsPosition.clear();
        mSelectedItemsCursorId.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount(){
        return mSelectedItemsPosition.size();
    }

    public List<Integer> getSelectedItems(){
        List items = new ArrayList<>(mSelectedItemsPosition.size());
        for (int i = 0; i < mSelectedItemsPosition.size(); i++){
            Log.d(TAG, "Seletion count " + mSelectedItemsPosition.size() + " cursorId " + mSelectedItemsCursorId.keyAt(i));
            items.add(mSelectedItemsPosition.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        int itemIndex = mSelectedItemsPosition.indexOfKey(position);
        int cursorItem = mSelectedItemsCursorId.keyAt(itemIndex);

        String selection = PaymentProviderContract.Payments.COLUMN_SCHEDULE_ID + "=?";
        ScheduleInfo schedule = DataManager.getInstance().getSchedule(cursorItem);
        String scheduleId = schedule.getScheduleId();
        String[] selectionArgs = {scheduleId};
        // delete associated payments
        Log.d(TAG, "deleting payment " + scheduleId);
        mContext.getContentResolver().delete(PaymentProviderContract.Payments.CONTENT_URI, selection, selectionArgs);

        Uri scheduleUri = ContentUris.withAppendedId(PaymentProviderContract.Schedules.CONTENT_URI, cursorItem);
        Log.d(TAG, "Deleting schedule " + scheduleUri);
        mContext.getContentResolver().delete(scheduleUri, null, null);

        notifyItemRemoved(position);
        resetCurrentIndex();
    }

    public void editData(int position) {
        int itemIndex = mSelectedItemsPosition.indexOfKey(position);
        int cursorItem = mSelectedItemsCursorId.keyAt(itemIndex);
        Intent intent = new Intent(mContext, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.SCHEDULE_MID, cursorItem);
        resetCurrentIndex();
        mContext.startActivity(intent);
    }

    private void resetCurrentIndex(){
        currentSelectedIndex = -1;
    }

    private void populateColumnPostions() {
        if(mCursor == null)
            return;
        mScheduleIdPos = mCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID);
        mMonthIdPos = mCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_MONTH_ID);
        mJamaatPos = mCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_MEMBER_JAMAATNAME);
        mTitlePos = mCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TITLE);
        mStatusPos = mCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ISCOMPLETE);
        mSubtotalPos = mCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALAMOUNT);
        mTotalPayersPos = mCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_TOTALPAYERS);
        mIdPos = mCursor.getColumnIndex(PaymentProviderContract.Schedules._ID);
    }

    public void changeCursor(Cursor cursor){
        if(mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPostions();
        notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public ScheduleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = mLayoutInflater.inflate(R.layout.item_schedule_list, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
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

        holder.itemView.setActivated(mSelectedItemsPosition.get(position, false));

        // Set onTouchListener on the holder
        ((ViewHolder)holder).parentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Touch Event", "touch press detected");
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
        Log.d("Ondown", "On down pressed");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        Log.d("onSingleTapUp", "create intent PaymentListActivity");
        Intent intent = new Intent(mContext, PaymentListActivity.class);
        intent.putExtra(PaymentListActivity.SCHEDULE_ID, mSelectedHolder.mScheduleId);
        mContext.startActivity(intent);
//        Snackbar.make(v, mScheduleTitle.getText(), Snackbar.LENGTH_LONG).show();

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        View view = mSelectedHolder.parentView;
        mListener.onItemClicked(mSelectedHolder.getAdapterPosition(),mSelectedHolder.mId );
        (view).performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void setClickAdapter(ScheduleClickAdapterListener listener){
        mListener = listener;
    }

    public int getScheduleCursorID(int position) {
        int itemIndex = mSelectedItemsPosition.indexOfKey(position);
        int cursorItem = mSelectedItemsCursorId.keyAt(itemIndex);
        return cursorItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mScheduleTitle;
        public final TextView mTextMonth;
        public final TextView mTextTotalPayers;
        public final TextView mTextScheduleTotalAmount;
        public final TextView mTextJamaatName;
        public String mScheduleId;
        FrameLayout parentView;
        private int mId;
        private final TextView mTextCompletionStatus;


        public ViewHolder(View itemView) {
            super(itemView);

            mScheduleTitle = (TextView) itemView.findViewById(R.id.text_schedule_title);
            mTextMonth = (TextView) itemView.findViewById(R.id.text_month_text);
            mTextTotalPayers = (TextView) itemView.findViewById(R.id.text_payers_number_text);
            mTextScheduleTotalAmount = (TextView) itemView.findViewById(R.id.text_amount_text);
            mTextCompletionStatus = (TextView) itemView.findViewById(R.id.text_status_text);
            mTextJamaatName = (TextView) itemView.findViewById(R.id.text_jamaat_name_text);
            parentView = (FrameLayout) itemView.findViewById(R.id.frame_layout);

        }

    }
}
