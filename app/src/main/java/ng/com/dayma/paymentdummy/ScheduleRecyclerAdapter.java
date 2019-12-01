package ng.com.dayma.paymentdummy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import ng.com.dayma.paymentdummy.data.PaymentDatabaseContract.ScheduleInfoEntry;
import ng.com.dayma.paymentdummy.touchhelpers.ItemTouchHelperAdapter;

/**
 * Created by Ahmad on 7/31/2019.
 */

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>
        implements ItemTouchHelperAdapter, View.OnTouchListener,GestureDetector.OnGestureListener{
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
    private ItemTouchHelper mTouchHelper;
    private GestureDetector mGestureDetector;
    private ViewHolder mSelectedHolder;
    private ClickAdapterListener mListener;

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
        Log.i(TAG, "selection for delete is "+ position + " the cursor value is " + mSelectedItemsCursorId.keyAt(itemIndex));
        int cursorItem = mSelectedItemsCursorId.keyAt(itemIndex);
        mListener.onDeleteSchedule(cursorItem);

        notifyItemRemoved(position);
        resetCurrentIndex();
    }

    public void editData(int position) {
//        Intent intent = new Intent(mContext, ScheduleActivity.class);
//        intent.putExtra(ScheduleActivity.SCHEDULE_MID, mSelectedHolder.mId);
//        mContext.startActivity(intent);
        resetCurrentIndex();
    }

    private void resetCurrentIndex(){
        currentSelectedIndex = -1;
    }

    private void populateColumnPostions() {
        if(mCursor == null)
            return;
        mScheduleIdPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_SCHEDULE_ID);
        mMonthIdPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_MONTH_ID);
        mJamaatPos = mCursor.getColumnIndex(ScheduleInfoEntry.COLUMN_MEMBER_JAMAATNAME);
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
                Log.d("Action_Down", "touch press detected");
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
        if(mListener.onSingleClick()){
            mListener.onItemClicked(mSelectedHolder.getAdapterPosition(), mSelectedHolder.mId);
            return false;
        }
        Log.d("onSingleTapUp", "On single tap detected");
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
//        if(mActionMode != null){
//            return;
//        }
//        mActionMode = ((AppCompatActivity)view.getContext()).startActionMode(mActionModecallbacks);
//        int idx = mSelectedHolder.getAdapterPosition();
//        Log.d("On Long press", "Long pressed on");
////        onItemSelected(idx);
//        this.onItemSelected(mSelectedHolder.getAdapterPosition());

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onItemSwiped(int position) {
        Log.d("onSwiped", "on swiped notified");

    }

    @Override
    public void onItemSelected(int position) {
        Log.d("ItemSelected", "Item is selected");

    }

    public void setTouchHelper(ItemTouchHelper touchHelper){
        mTouchHelper = touchHelper;
    }

    public void setClickAdapter(ClickAdapterListener listener){
        mListener = listener;
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

    public interface ClickAdapterListener {

        void onItemClicked(int adapterPosition, long cursorDataId);

        boolean onSingleClick();
        void onDeleteSchedule(int id);
    }
}
