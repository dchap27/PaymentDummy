package ng.com.dayma.paymentdummy;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;

public class MainActivityViewModel extends ViewModel {

    boolean isNewlyCreated = true;

    public static final String NAV_DRAWER_DISPLAY_SELECTION_NAME = "ng.com.dayma.paymentdummy.NAVDRAWERDISPLAYSELECTIONNAME";
    int navDrawerDisplaySelection = R.id.nav_schedules;

    public void saveState(Bundle outState) {
        outState.putInt(NAV_DRAWER_DISPLAY_SELECTION_NAME, navDrawerDisplaySelection);
    }

    public void restoreState(Bundle savedInstanceState) {
        navDrawerDisplaySelection = savedInstanceState.getInt(NAV_DRAWER_DISPLAY_SELECTION_NAME);
    }
}
