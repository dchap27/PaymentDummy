package ng.com.dayma.paymentdummy.MyViewModels;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;

public class ScheduleListViewModel extends ViewModel {

    public boolean isNewlyCreated = true;
    private static String ORIGINAL_MONTHID = "ng.com.dayma.paymentdummy.MyViewModels.ORIGINAL_MONTHID";

    public String monthId = null;

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_MONTHID, monthId);
    }

    public void restoreState(Bundle savedInstanceState) {
        savedInstanceState.getString(ORIGINAL_MONTHID);
    }
}
