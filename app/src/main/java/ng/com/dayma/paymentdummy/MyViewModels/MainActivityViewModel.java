package ng.com.dayma.paymentdummy.MyViewModels;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;

import java.util.List;

import ng.com.dayma.paymentdummy.R;

public class MainActivityViewModel extends ViewModel {

    public boolean isNewlyCreated = true;

    private static final String NAV_DRAWER_DISPLAY_SELECTION_NAME = "ng.com.dayma.paymentdummy.NAVDRAWERDISPLAYSELECTIONNAME";
    public int navDrawerDisplaySelection = R.id.nav_schedules;
    public String mJamaatName;
    public List<String> loadedJamaats;
    public String memberId;
    public String jamaatToUpdate;

    public void saveState(Bundle outState) {
        outState.putInt(NAV_DRAWER_DISPLAY_SELECTION_NAME, navDrawerDisplaySelection);
    }

    public void restoreState(Bundle savedInstanceState) {
        navDrawerDisplaySelection = savedInstanceState.getInt(NAV_DRAWER_DISPLAY_SELECTION_NAME);
    }
}
