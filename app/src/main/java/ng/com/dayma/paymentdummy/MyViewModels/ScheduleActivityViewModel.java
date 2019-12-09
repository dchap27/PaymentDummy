package ng.com.dayma.paymentdummy.MyViewModels;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

import ng.com.dayma.paymentdummy.DataManager;

public class ScheduleActivityViewModel extends ViewModel {

    private static final String ORIGINAL_MID = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_MID";
    private static final String ORIGINAL_MONTH_LIST = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_MONTH_LIST";
    private static final String ORIGINAL_JAMAALIST_LIST = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_JAMAALIST_LIST";
    private static final String SCHEDULE_URI = "ng.com.dayma.paymentdummy.MyViewModels.SCHEDULE_URI";
    public boolean isNewlyCreated = true;
    public ArrayList<String> months;
    public ArrayList<String> jamaatList = new ArrayList();
    public long mId;
    public Uri scheduleUri;

    public ArrayList<String> getMonths() {
        months = DataManager.getInstance().getMonthsOnly();
        return months;
    }


    public ArrayList<String> getYears() {
        return DataManager.getInstance().getYearsOnly();
    }

    public void restoreState(Bundle savedInstanceState) {
        savedInstanceState.getStringArrayList(ORIGINAL_MONTH_LIST);
        savedInstanceState.getStringArrayList(ORIGINAL_JAMAALIST_LIST);
        savedInstanceState.getLong(ORIGINAL_MID);
        String stringScheduleUri = savedInstanceState.getString(SCHEDULE_URI);
        scheduleUri = Uri.parse(stringScheduleUri);

    }

    public void saveState(Bundle outState) {
        outState.putStringArrayList(ORIGINAL_MONTH_LIST, months);
        outState.putStringArrayList(ORIGINAL_JAMAALIST_LIST, jamaatList);
        outState.putLong(ORIGINAL_MID, mId);
        outState.putString(SCHEDULE_URI, scheduleUri.toString());
    }
}
