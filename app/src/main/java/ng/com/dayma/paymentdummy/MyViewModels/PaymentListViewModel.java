package ng.com.dayma.paymentdummy.MyViewModels;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;

import ng.com.dayma.paymentdummy.DataManager;
import ng.com.dayma.paymentdummy.ScheduleInfo;

public class PaymentListViewModel extends ViewModel {

    private static final String ORIGINAL_MID = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_MID";
    public boolean isNewlyCreated = true;

    private static String ORIGINAL_SCHEDULE_ID = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_SCHEDULE_ID";
    private static String ORIGINAL_SCHEDULE_INFO = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_SCHEDULE_INFO";
    private String scheduleId;
    private ScheduleInfo schedule;
    private long mId;

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

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_SCHEDULE_ID, scheduleId);
        outState.putParcelable(ORIGINAL_SCHEDULE_INFO, schedule);
        outState.putInt(ORIGINAL_MID, mId);
    }

    public void restoreState(Bundle savedInstanceState) {
        savedInstanceState.getString(ORIGINAL_SCHEDULE_ID);
        savedInstanceState.getParcelable(ORIGINAL_SCHEDULE_INFO);
        savedInstanceState.getInt(ORIGINAL_MID);
    }
}
