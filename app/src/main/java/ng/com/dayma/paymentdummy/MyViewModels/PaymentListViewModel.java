package ng.com.dayma.paymentdummy.MyViewModels;

import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import ng.com.dayma.paymentdummy.DataManager;
import ng.com.dayma.paymentdummy.ScheduleInfo;
import ng.com.dayma.paymentdummy.data.PaymentProviderContract;

public class PaymentListViewModel extends ViewModel {

    private static final String ORIGINAL_MID = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_MID";
    public boolean isNewlyCreated = true;

    private static String ORIGINAL_SCHEDULE_ID = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_SCHEDULE_ID";
    private static String ORIGINAL_SCHEDULE_INFO = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_SCHEDULE_INFO";
    private String scheduleId;
    private ScheduleInfo schedule;
    private long mId;
    public String mInvoiceNumber;

    public String getScheduleId(){ return scheduleId;}
    public void setScheduleId(String id){
        scheduleId = id;
    }
    public void setId(long id){
        mId = id;
    }

    public long getId(){ return mId; }
    public ScheduleInfo getSchedule(String scheduleId) {
        schedule = DataManager.getInstance().getSchedule(scheduleId);
        return schedule;
    }

    public void getInvoiceNumber(final Context context){

        AsyncTask task = new AsyncTask() {

            private String mInvoice;

            @Override
            protected Object doInBackground(Object[] objects) {
                String[] projection = {
                        PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID,
                        PaymentProviderContract.Schedules.COLUMN_SCHEDULE_INVOICE,
                };
                String selection = PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ID + "=? AND "+
                        PaymentProviderContract.Schedules.COLUMN_SCHEDULE_ISCOMPLETE + "=?";
                String[] selectionArgs = { scheduleId, "2" };
                Cursor cursor = context.getContentResolver().query(PaymentProviderContract.Schedules.CONTENT_URI,
                        projection,selection,selectionArgs,null);
                if(cursor.moveToFirst()){
                    int invoicePos = cursor.getColumnIndex(PaymentProviderContract.Schedules.COLUMN_SCHEDULE_INVOICE);
                    mInvoice = cursor.getString(invoicePos);
                }
                PaymentListViewModel.this.mInvoiceNumber = mInvoice;
                return null;
            }
        };
        task.execute();

    }

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_SCHEDULE_ID, scheduleId);
        outState.putParcelable(ORIGINAL_SCHEDULE_INFO, schedule);
        outState.putLong(ORIGINAL_MID, mId);
    }

    public void restoreState(Bundle savedInstanceState) {
        savedInstanceState.getString(ORIGINAL_SCHEDULE_ID);
        savedInstanceState.getParcelable(ORIGINAL_SCHEDULE_INFO);
        savedInstanceState.getLong(ORIGINAL_MID);
    }
}
