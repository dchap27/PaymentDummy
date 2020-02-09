package ng.com.dayma.paymentdummy;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import ng.com.dayma.paymentdummy.data.PaymentProviderContract;
import ng.com.dayma.paymentdummy.touchhelpers.ItemTouchHelperAdapter;
import ng.com.dayma.paymentdummy.touchhelpers.RecyclerClickAdapterListener;

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
    public final String TAG = getClass().getSimpleName();
    private ItemTouchHelper mTouchHelper;
    private RecyclerClickAdapterListener mListener;
    private ViewHolder mSelectedHolder;
    private GestureDetector mGestureDetector;
    private DataManager.LoadFromDatabase mLoadFromDatabase;

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
        int totalSchedules = DataManager.getInstance().getScheduleCount(monthId);
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
        Log.d("ON SWIPE", "Delete Month and corresponding Schedules and associated payments");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(
                "Are you sure you want to delete schedules for "+mSelectedHolder.mMonthId +"?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMonthRecords();
            }
        });
        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void deleteMonthRecords() {
        String[] scheduleProjection = {
                PaymentProviderContract.Schedules.COLUMN_MONTH_ID,
                PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID,
        };
        String scheduleSelection = PaymentProviderContract.Schedules.COLUMN_MONTH_ID + "=?";
        String[] scheduleSelectionArgs = {mSelectedHolder.mMonthId};
        Cursor scheduleCursor = mContext.getContentResolver().query(PaymentProviderContract.Schedules.CONTENT_URI,
                scheduleProjection, scheduleSelection, scheduleSelectionArgs, null);
        int scheduleIdPos = scheduleCursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID);
        boolean more = scheduleCursor.moveToFirst();
        while (more) {
            String scheduleId = scheduleCursor.getString(scheduleIdPos);
            // delete payment with scheduleId
            String paymentSelection = PaymentProviderContract.Payments.COLUMN_SCHEDULE_ID + "=?";
            String[] paymentSelectionArgs = { scheduleId };
            Log.d(TAG, "Deleting payment "+ scheduleId);
            mContext.getContentResolver().delete(PaymentProviderContract.Payments.CONTENT_URI, paymentSelection,
                    paymentSelectionArgs);
            more = scheduleCursor.moveToNext();
        }
        scheduleCursor.close();
        // delete Schedule
        Log.d(TAG, "Deleting schedule "+ mSelectedHolder.mMonthId);
        int numOfSchedules = mContext.getContentResolver().delete(
                PaymentProviderContract.Schedules.CONTENT_URI, scheduleSelection, scheduleSelectionArgs);
        // delete Month from MONTH_TABLE
        Uri mMonthUri = ContentUris.withAppendedId(PaymentProviderContract.Months.CONTENT_URI, mSelectedHolder.mId);
        Log.d(TAG, "Deleting Month "+ mMonthUri);
        mContext.getContentResolver().delete(mMonthUri, null, null);
        mLoadFromDatabase = new DataManager.LoadFromDatabase();
        mLoadFromDatabase.execute(mContext);

        String displayVerbMessage = " Schedule";
        if(numOfSchedules>1){
            displayVerbMessage = " Schedules";
        }
        Toast.makeText(mContext, numOfSchedules+displayVerbMessage+" deleted",Toast.LENGTH_LONG).show();
    }

    public void setTouchHelper(ItemTouchHelper touchHelper){
        mTouchHelper = touchHelper;
    }

    public void setClickAdapter(RecyclerClickAdapterListener listener){
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

        }
    }
}
